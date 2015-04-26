/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

#include <pthread.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <semaphore.h>
#include "sharedMemoryQueue.h"

#define TEST
#define MAX_ENTRIES 2097152ULL
#define NULL_ENTRY  -1

//Queue that consists of a head and tail that stores active entries.
typedef struct queue
{
    int head;
    int tail;
    pthread_mutex_t mutex;
    sem_t sem;
} queue_t;

queue_t *Q               = NULL;
queue_t *ResponseQ       = NULL; //Handles response from client
queue_t *freeQ           = NULL; //Handles unused entries
queueEntry_t *entryArray = NULL;

#ifdef	__cplusplus
extern "C" {
#endif

//Methods used only in Queue.c
void q_validate( queue_t *q );
void q_add( queue_t *q, queueEntry_t *entry );
queueEntry_t *q_remove( queue_t *q );

#ifdef	__cplusplus
}
#endif


/* Initialize shared memory lock. */
void q_init_lock( pthread_mutex_t *mutex )
{
    pthread_mutexattr_t attr;
    pthread_mutexattr_init( &attr );
    pthread_mutexattr_setpshared( &attr, PTHREAD_PROCESS_SHARED );
    pthread_mutex_init( mutex, &attr);
}

/* Initialize shared memory semaphore. */
void q_init_sem( sem_t *s )
{
    sem_init( s, 1, 0 );
}

//Initialize queue along with lock and semaphore
queue_t* q_init_q( const char *queueName )
{
    //Pointer to shared memory area
    queue_t *q = (queue_t *)shm_init( queueName, sizeof( queue_t ) );
    
    //Initialize shared memory
    q->head = NULL_ENTRY;
    q->tail = NULL_ENTRY;
    q_init_lock( &q->mutex );
    q_init_sem( &q->sem );
    
    //Store pointer to q (give back to caller)
    return q;
}

/* Initialize shared memory for server. Initialize all shared memory areas. */
void q_server_init( void )
{
    int i;
    
    //Initialize queues
    Q         = q_init_q( "/Queue" );
    ResponseQ = q_init_q( "/ResponseQ" );
    freeQ     = q_init_q( "/FreeQueue" );
    
    // Create array of queueEntries and initialize each entry.
    // Build linked list of free list items (all go on the freeQ
    // since they are not used at first)
    entryArray = (queueEntry_t *)shm_init( "/QueueEntries",
                                           MAX_ENTRIES * sizeof( queueEntry_t ) );
    for( i = 0; i < MAX_ENTRIES; i++ )
    {
        entryArray[i].me = i;
        
        if( i == 0 )
        {
            entryArray[i].prev = NULL_ENTRY;
        }
        else
        {
            entryArray[i].prev = i-1;
        }
        
        if( i == (MAX_ENTRIES-1) )
        {
            entryArray[i].next = NULL_ENTRY;
        }
        else
        {
            entryArray[i].next = i+1;
        }
        
        memset( entryArray[i].data, 0, QUEUE_ENTRY_DATA_SIZE );
    }
    freeQ->head = 0;
    freeQ->tail = MAX_ENTRIES-1;
    //printf( "q_server_init(): head = %d, tail = %d.", freeQ->head, freeQ->tail );
}

/* Initialize client thread. Attach to shared memory areas. */
void q_client_init( void )
{
    Q          = (queue_t *)shm_init( "/Queue",     sizeof( queue_t ) );
    ResponseQ  = (queue_t *)shm_init( "/ResponseQ", sizeof( queue_t ) );
    freeQ      = (queue_t *)shm_init( "/FreeQueue", sizeof( queue_t ) );
    entryArray = (queueEntry_t *)shm_init( "/QueueEntries",
                                           MAX_ENTRIES * sizeof(queueEntry_t) );
}

/* Check if queue corrupt or not. */
void q_validate( queue_t *q )
{
#ifdef TEST //Check to see if head or tail corrupt.
    if( ( (q->head == NULL_ENTRY) && (q->tail != NULL_ENTRY) ) || ( (q->head != NULL_ENTRY) && (q->tail == NULL_ENTRY) ) )
    {
        printf( "queue corrupt: q->head = %d, q->tail = %d\n", q->head, q->tail );
        abort();
    }
#endif    
}

/* Free unused entry and add to free queue. */
void q_free_entry( queueEntry_t *entry )
{
    //printf( "q_free_entry( enter )\n" );
    pthread_mutex_lock( &freeQ->mutex );
    {
	//printf( "q_free_entry(): freeQ->head = %d, freeQ->tail = %d, sizeof entryArray = %zd, number of elements = %ld.\n",
		//freeQ->head, freeQ->tail, sizeof( entryArray ), sizeof( entryArray ) / sizeof( queueEntry_t ) );
        q_validate( freeQ );
        
        if( freeQ->head != NULL_ENTRY )
        {
            entryArray[freeQ->head].prev = entry->me;
            entry->next                  = freeQ->head;
            entry->prev                  = NULL_ENTRY;
            freeQ->head                  = entry->me;
        }
        else
        {
            entry->next = entry->prev = NULL_ENTRY;
            freeQ->head = freeQ->tail = entry->me;
        }
        q_validate( freeQ );
    }
    pthread_mutex_unlock( &freeQ->mutex );
    //printf( "q_free_entry( exit )\n" );
}

/* Get unused entry from free queue to recycle. */
queueEntry_t *q_alloc_entry( void )
{
    queueEntry_t *entry = NULL;
    //printf( "q_alloc_entry( enter )\n" );
    //Serialize access to free queue
    pthread_mutex_lock( &freeQ->mutex );
    {
	//printf( "q_alloc_entry(): freeQ->head = %d, freeQ->tail = %d, sizeof entryArray = %zd, number of elements = %ld.\n",
		//freeQ->head, freeQ->tail, sizeof( entryArray ), sizeof( entryArray ) / sizeof( queueEntry_t ) );
        q_validate( freeQ ); //Check for corruption of queue
        if( freeQ->head != NULL_ENTRY )
        {
            entry = &entryArray[freeQ->head];
            freeQ->head = entry->next;
            entryArray[freeQ->head].prev = NULL_ENTRY;
            
            if( freeQ->head == NULL_ENTRY )
            {
                freeQ->tail = NULL_ENTRY;
            }
        }
        q_validate( freeQ ); //Check for corruption of queue
    }
    pthread_mutex_unlock( &freeQ->mutex );
    
    if( entry != NULL )
    {
        entry->next = NULL_ENTRY;
        entry->prev = NULL_ENTRY;
    }

    //printf( "q_alloc_entry( exit )\n" );
    return entry;
}

void q_add( queue_t *q, queueEntry_t *entry )
{
    //printf( "q_add( enter )\n" );
    pthread_mutex_lock( &q->mutex );
    {
        q_validate( q );

	//printf( "q->head = %d, q->tail = %d\n", q->head, q->tail );
        if( q->head != NULL_ENTRY )
        {
            entryArray[q->tail].next = entry->me;
            entry->prev              = q->tail;
            q->tail                  = entry->me;
            entry->next              = NULL_ENTRY;
        }
        else
        {
            q->head =q->tail = entry->me;
            entry->next = entry->prev = NULL_ENTRY;
        }
        q_validate( q );
    }
    pthread_mutex_unlock( &q->mutex );
    //printf( "q_add( exit )\n" );
}

queueEntry_t *q_remove( queue_t *q )
{
    queueEntry_t *entry = NULL;
    //printf( "q_remove( enter )\n" );
    //printf( "q = %p.\n", q );
    pthread_mutex_lock( &q->mutex );
    {
        q_validate( q );
        //printf( "Validate passed.\n" );
        //printf( "q->head = %d\n", q->head );
        if( q->head != NULL_ENTRY )
        {
            entry = &entryArray[q->head];
            //printf( "entry->next = %d.\n", entry->next );
            q->head = entry->next;
            if( q->head != NULL_ENTRY )
            {
                entryArray[q->head].prev = NULL_ENTRY;
            }
            else
            {
                q->tail = NULL_ENTRY;
            }
        }
    }
    pthread_mutex_unlock( &q->mutex );

    if( entry != NULL )
    {
        entry->next = entry->prev = NULL_ENTRY;
    }
    
    //printf( "q_remove( exit )\n" );
    return entry;
}

//Enqueue entry to data queue
void q_enqueue( queueEntry_t *entry )
{
    //printf( "q_enqueue( enter )\n" );
    q_add( Q, entry );
    //printf( "q_enqueue( exit )\n" );
}

//Dequeue entry from data queue
queueEntry_t *q_dequeue( void )
{
    //printf( "q_dequeue( enter )\n" );
    queueEntry_t *e = q_remove( Q );
    //printf( "q_dequeue( exit )\n" );
    return( e );
}

void q_notify( void )
{
    //printf( "q_notify( enter )\n" );
    sem_post( &Q->sem );
    //printf( "q_notify( exit )\n" );
}
    
void q_wait( void )
{
    //printf( "q_wait( enter )\n" );
    sem_wait( &Q->sem );
    //printf( "q_wait( exit )\n" );
}

//Enqueue entry to response queue
void q_enqueueResponse( queueEntry_t *entry )
{
    //printf( "q_enqueueResponse( enter )\n" );
    q_add( ResponseQ, entry );
    //printf( "q_enqueueResponse( exit )\n" );
}

//Dequeue entry from response queue
queueEntry_t *q_dequeueResponse( void )
{
    //printf( "q_dequeueResponse( enter )\n" );
    queueEntry_t *e = q_remove( ResponseQ );
    //printf( "q_dequeueResponse( exit )\n" );
    return( e );
}

void q_notifyResponse( void )
{
    //printf( "q_notifyResponse( enter )\n" );
    sem_post( &ResponseQ->sem );
    //printf( "q_notifyResponse( exit )\n" );
}

void q_waitResponse( void )
{
    //printf( "q_waitResponse( enter )\n" );
    sem_wait( &ResponseQ->sem );
    //printf( "q_waitResponse( exit )\n" );
}

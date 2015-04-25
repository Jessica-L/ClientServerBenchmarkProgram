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
#include "SharedMemoryQueue.h"

#define TEST
#define MAX_ENTRIES 1024
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
        printf( "queue corrupt with errno %d<%s>\n", errno, strerror(errno) );
        abort();
    }
#endif    
}

/* Free unused entry and add to free queue. */
void q_free_entry( queueEntry_t *entry )
{
    pthread_mutex_lock( &freeQ->mutex );
    {
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
}

/* Get unused entry from free queue to recycle. */
queueEntry_t *q_alloc_entry( void )
{
    queueEntry_t *entry = NULL;
    //Serialize access to free queue
    pthread_mutex_lock( &freeQ->mutex );
    {
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

    return entry;
}

void q_add( queue_t *q, queueEntry_t *entry )
{
    pthread_mutex_lock( &q->mutex );
    {
        q_validate( q );
        
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
}

queueEntry_t *q_remove( queue_t *q )
{
    queueEntry_t *entry = NULL;
    pthread_mutex_lock( &q->mutex );
    {
        q_validate( q );
        
        if( q->head != NULL_ENTRY )
        {
            entry = &entryArray[q->head];
            q->head = entry->next;
            entryArray[q->head].prev = NULL_ENTRY;
        }
    }
    pthread_mutex_unlock( &q->mutex );

    if( entry != NULL )
    {
        entry->next = entry->prev = NULL_ENTRY;
    }
    return entry;
}

//Enqueue entry to data queue
void q_enqueue( queueEntry_t *entry )
{
    q_add( Q, entry );
}

//Dequeue entry from data queue
queueEntry_t *q_dequeue( void )
{
    return( q_remove( Q ) );
}

void q_notify( void )
{
    sem_post( &Q->sem );
}
    
void q_wait( void )
{
    sem_wait( &Q->sem );
}

//Enqueue entry to response queue
void q_enqueueResponse( queueEntry_t *entry )
{
    q_add( ResponseQ, entry );
}

//Dequeue entry from response queue
queueEntry_t *q_dequeueResponse( void )
{
    return( q_remove( ResponseQ ) );
}

void q_notifyResponse( void )
{
    sem_post( &ResponseQ->sem );
}

void q_waitResponse( void )
{
    sem_post( &ResponseQ->sem );
}

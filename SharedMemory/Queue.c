/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

#include <stdlib.h>
#include <pthread.h>
#include <errno.h>
#include "SharedMemoryQueue.h"

#define TEST
#define MAX_ENTRIES 1024
#define NULL_ENTRY  -1

typedef struct queue
{
    queueEntry_t *head;
    queueEntry_t *tail;
    pthread_mutex_t mutex;
} queue_t;

queue_t *Q               = NULL;
queue_t *freeQ           = NULL;
queueEntry_t *entryArray = NULL;

/* Initialize shared memory lock. */
void q_init_lock( pthread_mutex_t *mutex )
{
    pthread_mutexattr_t attr;
    pthread_mutexattr_init( &attr );
    pthread_mutexattr_setpshared( &attr, PTHREAD_PROCESS_SHARED );
    pthread_mutex_init( mutex, &attr);
}

/* Initialize shared memory for server. */
void q_server_init( void )
{
    //Queue that consists of a head and tail that stores active entries.
    Q       = (queue_t *)shm_init( "/Queue", sizeof(queue_t) );
    Q->head = NULL_ENTRY;
    Q->tail = NULL_ENTRY;
    q_init_lock( Q->mutex );
    
    //Free queue that consists of a head and tail that stores unused entries.
    freeQ       = (queue_t *)shm_init( "/FreeQueue", sizeof(queue_t) );
    freeQ->head = NULL_ENTRY;
    freeQ->tail = NULL_ENTRY;
    q_init_lock( freeQ->mutex );
    
    // Create array of queueEntries and initialize each entry.
    entryArray = (queueEntry_t *)shm_init( "/QueueEntries", MAX_ENTRIES*sizeof(queueEntry_t) );
    for( int i = 0; i < MAX_ENTRIES; i++ )
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

/* Initialize client thread. */
void q_client_init( void )
{
    Q          = (queue_t *)shm_init( "/Queue", sizeof(queue_t) );
    freeQ      = (queue_t *)shm_init( "/FreeQueue", sizeof(queue_t) );
    entryArray = (queueEntry_t *)shm_init( "/QueueEntries", MAX_ENTRIES*sizeof(queueEntry_t) );
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

/* Add unused entry to free queue. */
void q_free_entry( queueEntry_t *entry )
{
    pthread_mutex_lock( freeQ->mutex );
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
    pthread_mutex_unlock( freeQ->mutex );
}

/* Get unused entry from free queue to recycle. */
queueEntry_t *q_alloc_entry( void )
{
    queueEntry_t *entry = NULL;
    pthread_mutex_lock( freeQ->mutex );
    {
        q_validate( freeQ );
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
        q_validate( freeQ );
    }
    pthread_mutex_unlock( freeQ->mutex );
    
    if( entry != NULL )
    {
        entry->next = NULL_ENTRY;
        entry->prev = NULL_ENTRY;
    }

    return entry;
}

void q_enqueue( queueEntry_t *entry )
{
    pthread_mutex_lock( Q->mutex );
    {
        q_validate( Q );
        if( Q->head != NULL_ENTRY )
        {
            entryArray[Q->tail].next = entry->me;
            entry->prev              = Q->tail
            Q->tail                  = entry->me;
            entry->next              = NULL_ENTRY;
        }
        else
        {
            Q->head = Q->tail = entry->me;
            entry->next = entry->prev = NULL_ENTRY;
        }
        q_validate( Q );
    }
    pthread_mutex_unlock( Q->mutex );
}

queueEntry_t *q_dequeue( void )
{
    queueEntry_t *entry = NULL;
    pthread_mutex_lock( Q->mutex );
    {
        q_validate( Q );
        
        if( Q->head != NULL_ENTRY )
        {
            entry = &entryArray[Q->head];
            Q->head = entry->next;
            entryArray[Q->head].prev = NULL_ENTRY;
        }
    }
    pthread_mutex_unlock( Q->mutex );

    if( entry != NULL )
    {
        entry->next = entry->prev = NULL_ENTRY;
    }
    return entry;
}




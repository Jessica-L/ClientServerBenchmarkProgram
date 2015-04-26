/* 
 * File:   sharedMemoryQueue.h
 * Author: christopher
 *
 * Created on April 25, 2015, 1:56 PM
 */

#ifndef SHAREDMEMORYQUEUE_H
#define	SHAREDMEMORYQUEUE_H

#include <semaphore.h>

#ifdef	__cplusplus
extern "C" {
#endif

#define QUEUE_ENTRY_DATA_SIZE 1024

typedef struct queueEntry
{
    char data[QUEUE_ENTRY_DATA_SIZE];
    int prev;
    int next;
    int me;
} queueEntry_t;

//Initialization methods
void *shm_init( const char *name, size_t size );
void q_init_lock( pthread_mutex_t *mutex );
void q_init_sem( sem_t *s );
void q_server_init( void );
void q_client_init( void );

//Methods to handle unused entries
void q_free_entry( queueEntry_t *entry ); //Free unused entry; add to free queue
queueEntry_t *q_alloc_entry( void ); //Allocate free entry to reuse it

//Data queue methods
void q_enqueue( queueEntry_t *entry );
queueEntry_t *q_dequeue( void );
void q_notify( void );
void q_wait( void );

//Response queue methods
void q_enqueueResponse( queueEntry_t *entry );
queueEntry_t *q_dequeueResponse( void );
void q_notifyResponse( void );
void q_waitResponse( void );


#ifdef	__cplusplus
}
#endif

#endif	/* SHAREDMEMORYQUEUE_H */


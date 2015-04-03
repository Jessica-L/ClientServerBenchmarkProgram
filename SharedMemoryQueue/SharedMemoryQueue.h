/* 
 * File:   SharedMemoryQueue.h
 * Author: Jessica
 *
 * Created on March 31, 2015, 11:37 PM
 */

#ifndef SHAREDMEMORYQUEUE_H
#define	SHAREDMEMORYQUEUE_H

#ifdef	__cplusplus
extern "C" {
#endif

#define DATA_SIZE 1024

typedef struct queueEntry
{
    char data[DATA_SIZE];
    int prev;
    int next;
    int me;
} queueEntry_t;

void *shm_init( const char *name, size_t size );
void server_init();
void q_init_lock( pthread_mutex_t *mutex );
void q_server_init( void );
void q_client_init( void );
void q_validate( queue_t *q );
void q_free_entry( queueEntry_t *entry );
queueEntry_t *q_alloc_entry( void );
void q_enqueue( queueEntry_t *entry );
queueEntry_t *q_dequeue( void );


#ifdef	__cplusplus
}
#endif

#endif	/* SHAREDMEMORYQUEUE_H */


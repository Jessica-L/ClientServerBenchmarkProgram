/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

#ifndef SHAREDMEMORYQUEUE_H
#define	SHAREDMEMORYQUEUE_H

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


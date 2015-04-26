/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/mman.h>
#include <sys/stat.h> /* For mode constants */
#include <fcntl.h> /* For O_* constants */
#include <errno.h>

/* Initializes shared memory for a specific shared memory area. */
void *shm_init( const char *name, size_t size )
{
    void *shmPtr  = NULL;
    int truncate  = 1;
    int map       = 1;
    int shmHandle = shm_open( name, ( O_RDWR | O_CREAT | O_EXCL ), 0662 );
    
    if( shmHandle < 0 )
    {
        if( errno == EEXIST )
        {
            if( ( shmHandle = shm_open(name, O_RDWR, 0662) ) < 0 )
            {
                printf( "shm_open failed with errno %d<%s>\n", errno, strerror(errno) );
                map = 0;
            }
        }
        else
        {
            printf( "shm_open failed with errno %d<%s>\n", errno, strerror(errno) );
            map = 0;
        }
        truncate = 0;
    }
    
    if( truncate )
    {
        if( ftruncate( shmHandle, size ) < 0 )
        {
            printf( "ftruncate failed with errno %d<%s>\n", errno, strerror(errno) );
            close( shmHandle );
            map = 0;
        }
    }
    
    if( map )
    {
        shmPtr = mmap( NULL, size, ( PROT_READ | PROT_WRITE ), MAP_SHARED, shmHandle, 0);
        if( shmPtr == MAP_FAILED )
        {
            printf( "mmap failed with errno %d<%s>\n", errno, strerror(errno) );
            close( shmHandle );
            shmPtr = NULL;
        }
    }
    return shmPtr;
}




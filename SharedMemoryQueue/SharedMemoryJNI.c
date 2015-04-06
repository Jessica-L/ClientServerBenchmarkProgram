/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

#include <jni.h>
#include <stdio.h>
#include "SharedMemoryJNI.h"
#include "SharedMemoryQueue.h"
 
// Implementation of native method sayHello() of HelloJNI class
JNIEXPORT void JNICALL Java_clientserver_SharedMemoryJNI_initServer(JNIEnv *env, jobject thisObj)
{
   q_server_init( void );
}

// Implementation of native method initClient() of SharedMemoryJNI class
JNIEXPORT void JNICALL Java_clientserver_SharedMemoryJNI_initClient(JNIEnv *env, jobject thisObj)
{
   q_client_init( void );
}

// Implementation of native method sayHello() of HelloJNI class
JNIEXPORT void JNICALL Java_clientserver_SharedMemoryJNI_enqueue(JNIEnv *env, jobject thisObj, jstring data )
{
    size_t length = (size_t)env->GetStringLength( data );
    
    if( length > QUEUE_ENTRY_DATA_SIZE )
    {
        env->ThrowNew( Exception, "String Size Too Large" );
    }
    
    queueEntry_t *entry = q_alloc_entry( void );
    const char  *cStr  = env->GetStringChars( data, NULL );
    memcpy( entry->data, cStr, length );
    q_enqueue( entry );
    env->ReleaseStringChars( data, cStr );
}

// Implementation of native method sayHello() of HelloJNI class
JNIEXPORT jstring JNICALL Java_clientserver_SharedMemoryJNI_dequeue(JNIEnv *env, jobject thisObj)
{
   queueEntry_t *entry = q_dequeue( void );
   jstring  str = env->NewString( (const jchar *)entry->data, strlen( entry->data ) );
   q_free_entry( entry );
   return str;
}

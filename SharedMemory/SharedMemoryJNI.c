/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

#include <jni.h>
#include <stdio.h>
#include <string.h>
//#include "SharedMemoryJNI.h"
#include "SharedMemoryQueue.h"
 
// Implementation of native method sayHello() of HelloJNI class
JNIEXPORT void JNICALL
Java_clientserver_SharedMemoryJNI_initServer( JNIEnv *env, jobject thisObj )
{
   q_server_init();
}

// Implementation of native method initClient() of SharedMemoryJNI class
JNIEXPORT void JNICALL
Java_clientserver_SharedMemoryJNI_initClient( JNIEnv *env, jobject thisObj )
{
   q_client_init();
}

void
queueAdd( JNIEnv *env, jobject thisObj, jstring data,
          void (*enqueueFunc)( queueEntry_t *) )
{
    size_t length = (size_t)(*env)->GetStringLength( env, data );
    
    if( length > QUEUE_ENTRY_DATA_SIZE )
    {
        char *className = "java/lang/Exception";
        jclass exception = (*env)->FindClass( env, className );

       (*env)->ThrowNew( env, exception, "String Size Too Large" );
    }
    
    queueEntry_t *entry = q_alloc_entry();
    //const jchar  *cStr  = GetStringChars( env, data, NULL );

    const jchar  *cStr  = (*env)->GetStringChars( env, data, NULL );

    memcpy( entry->data, cStr, length );
    (*enqueueFunc)( entry );
    (*env)->ReleaseStringChars( env, data, cStr );
}
// Implementation of native method enqueue() of HelloJNI class
JNIEXPORT void JNICALL
Java_clientserver_SharedMemoryJNI_enqueue( JNIEnv *env, jobject thisObj,
                                           jstring data )
{
    queueAdd( env, thisObj, data, q_enqueue );
}

JNIEXPORT void JNICALL
Java_clientserver_SharedMemoryJNI_enqueueResp( JNIEnv *env, jobject thisObj,
                                               jstring data )
{
    queueAdd( env, thisObj, data, q_enqueueResponse );
}

jstring
queueRemove( JNIEnv *env, jobject thisObj, queueEntry_t *(*dequeueFunc)() )
{
   queueEntry_t *entry = (*dequeueFunc)();
   jstring  str = (*env)->NewString( env, (const jchar *)entry->data,
                                     strlen( entry->data ) );
   q_free_entry( entry );
   return str;
}

// Implementation of native method dequeue() of SharedMemoryJNI class
JNIEXPORT jstring JNICALL
Java_clientserver_SharedMemoryJNI_dequeue( JNIEnv *env, jobject thisObj )
{
    return( queueRemove( env, thisObj, q_dequeue ) );
}

JNIEXPORT jstring JNICALL
Java_clientserver_SharedMemoryJNI_dequeueResp( JNIEnv *env, jobject thisObj )
{
    return( queueRemove( env, thisObj, q_dequeueResponse ) );
}

// Implementation of native method shmNotify() of SharedMemoryJNI class
JNIEXPORT void JNICALL
Java_clientserver_SharedMemoryJNI_shmNotify( JNIEnv *env, jobject thisObj )
{
    q_notify();
}

// Implementation of native method shmWait() of SharedMemoryJNI class
JNIEXPORT void JNICALL
Java_clientserver_SharedMemoryJNI_shmWait( JNIEnv *env, jobject thisObj )
{
    q_wait();
}

// Implementation of native method shmNotify() of SharedMemoryJNI class
JNIEXPORT void JNICALL
Java_clientserver_SharedMemoryJNI_shmNotifyResp( JNIEnv *env, jobject thisObj )
{
    q_notifyResponse();
}

// Implementation of native method shmWait() of SharedMemoryJNI class
JNIEXPORT void JNICALL
Java_clientserver_SharedMemoryJNI_shmWaitResp( JNIEnv *env, jobject thisObj )
{
    q_waitResponse();
}
/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */

/*! \file NativeDetector.h
 *  \author Branislav Repcek
 *  \date 2005
 *
 * \brief Declarations of methods exported from the DLL.
 */
#ifndef _Included_NativeDetector
#define _Included_NativeDetector
 
#include <jni.h>

#ifdef __cplusplus
extern "C"
{
#endif

    /*
     * Class:     NativeDetector
     * Method:    nativeInitialize
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeInitialize(JNIEnv *, jobject);

    /*
     * Class:     NativeDetector
     * Method:    nativeExecute
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeExecute(JNIEnv *, jobject);

    /*
     * Class:     NativeDetector
     * Method:    nativeGetData
     * Signature: ()Ljava/lang/String;
     */
    JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetData(JNIEnv *, jobject);

    /*
     * Class:     NativeDetector
     * Method:    nativeGetMessages
     * Signature: ()Ljava/lang/String;
     */
    JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetMessages(JNIEnv *, jobject);

    /*
     * Class:     NativeDetector
     * Method:    nativeDestroy
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeDestroy(JNIEnv *, jobject);

    /*
     * Class:     NativeDetector
     * Method:    nativeGetEncoding
     * Signature: ()Ljava/lang/String;
     */
    JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetEncoding(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif

#endif

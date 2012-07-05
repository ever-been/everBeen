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

/*! \file NativeDetector.cpp
 *  \author Branislav Repcek
 *  \date 2005
 *
 *  \brief Definitions of library exports.
 */

#include <jni.h>
#include "../common/UnicodeString.h"
#include "LinuxDetector.h"
#include "NativeDetector.h"

//! Instance of the detector.
hwdet::LinuxDetector detector;

/*! Initialize detector.
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeInitialize(JNIEnv *, jobject) {

    return jboolean(detector.Initialize());
}

/*! Detect hardware/software features.
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeExecute(JNIEnv *, jobject) {

    return jboolean(detector.Detect());
}

/*! Get data retrieved by detector.
 */
JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetData(JNIEnv *env, jobject) {

    ::hwdet::StringStream stream;

    detector.WriteToStream(stream);
    
    return env->NewStringUTF(stream.str().c_str());
}

/*! Get messages string produced by the detector.
 */
JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetMessages(JNIEnv *env, jobject) {

    ::hwdet::StringStream stream;

    detector.WriteMessagesToStream(stream);

    return env->NewStringUTF(stream.str().c_str());
}

/*! Clean-up.
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeDestroy(JNIEnv *, jobject) {

    return jboolean(detector.Destroy());
}

/*! Get encoding used in the library.
 */
JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetEncoding(JNIEnv *env, jobject) {

    return env->NewStringUTF(detector.GetOutputEncoding().c_str());
}

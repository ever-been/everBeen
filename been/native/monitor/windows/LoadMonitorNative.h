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

/*! \file LoadMonitorNative.h
 *  \author Branislav Repcek
 *  \date 2. 10. 2006
 *
 *  \brief Declarations of methods exported from DLL.
 */

#include <jni.h>

#ifndef _Included_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative
#define _Included_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     cz.cuni.mff.been.hostmanager.load.LoadMonitorNative
 * Method:    nativeInitialize
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative_nativeInitialize(JNIEnv *, jobject);

/*
 * Class:     cz.cuni.mff.been.hostmanager.load.LoadMonitorNative
 * Method:    nativeTerminate
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative_nativeTerminate(JNIEnv *, jobject);

/*
 * Class:     cz.cuni.mff.been.hostmanager.load.LoadMonitorNative
 * Method:    nativeGetSample
 * Signature: ()Lcz/cuni/mff/been/hostmanager/load/LoadSample;
 */
JNIEXPORT jobject JNICALL Java_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative_nativeGetSample(JNIEnv *, jobject);

/*
 * Class:     cz_cuni_mff_been_hostmanager_load_LoadMonitorNative
 * Method:    nativeGetHardwareDescription
 * Signature: ()Lcz/cuni/mff/been/hostmanager/load/HardwareDescription;
 */
JNIEXPORT jobject JNICALL Java_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative_nativeGetHardwareDescription(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif

#endif

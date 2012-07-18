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

/*! \file FileSystemName.h
 *  \author Branislav Repcek
 *  \date 12. 12. 2006
 * 
 *  \brief Filesystem names.
 * 
 *  \note Original credits due to Michael Meskes, author of the stat from the coreutils.
 */

#ifndef FILE_SYSTEM_NAME_INCLUDED
#define FILE_SYSTEM_NAME_INCLUDED

#include "../common/UnicodeString.h"

#if !defined __linux__ && defined __GNU__
#include <hurd/hurd_types.h>
#endif

namespace hwdet
{
#if defined __linux__
    //! AFFS.
    const long FS_MAGIC_AFFS = 0xADFF;
    
    //! DEVPTS
    const long FS_MAGIC_DEVPTS = 0x1CD1;
    
    //! EXT
    const long FS_MAGIC_EXT = 0x137D;
    
    //! EXT2 OLD
    const long FS_MAGIC_EXT2_OLD = 0xEF51;
    
    //! EXT2
    const long FS_MAGIC_EXT2 = 0xEF53;
    
    //! JFS
    const long FS_MAGIC_JFS = 0x3153464a;
    
    //! XFS
    const long FS_MAGIC_XFS = 0x58465342;
    
    //! HPFS
    const long FS_MAGIC_HPFS = 0xF995E849;
    
    //! ISOFS
    const long FS_MAGIC_ISOFS = 0x9660;
    
    //! Windows ISOFS
    const long FS_MAGIC_ISOFS_WIN = 0x4000;
    
    //! Windows ISOFS
    const long FS_MAGIC_ISOFS_R_WIN = 0x4004;
    
    //! Minix
    const long FS_MAGIC_MINIX = 0x137F;
    
    //! Minix 3.0
    const long FS_MAGIC_MINIX_30 = 0x138F;
    
    //! Minix V2
    const long FS_MAGIC_MINIX_V2 = 0x2468;
    
    //! Minix V2 3.0
    const long FS_MAGIC_MINIX_V2_30 = 0x2478;
    
    //! MSDOS
    const long FS_MAGIC_MSDOS = 0x4d44;
    
    //! FAT
    const long FS_MAGIC_FAT = 0x4006;
    
    //! NCP
    const long FS_MAGIC_NCP = 0x564c;
    
    //! NFS
    const long FS_MAGIC_NFS = 0x6969;
    
    //! PROCFS
    const long FS_MAGIC_PROC = 0x9fa0;
    
    //! SMB
    const long FS_MAGIC_SMB = 0x517B;
    
    //! Xenix
    const long FS_MAGIC_XENIX = 0x012FF7B4;
    
    //! SYS V4
    const long FS_MAGIC_SYSV4 = 0x012FF7B5;
    
    //! SYS V2
    const long FS_MAGIC_SYSV2 = 0x012FF7B6;
    
    //! COH
    const long FS_MAGIC_COH = 0x012FF7B7;
    
    //! UFS
    const long FS_MAGIC_UFS = 0x00011954;
    
    //! XIAFS
    const long FS_MAGIC_XIAFS = 0x012FD16D;
    
    //! NTFS
    const long FS_MAGIC_NTFS = 0x5346544e;
    
    //! TMPFS
    const long FS_MAGIC_TMPFS = 0x1021994;
    
    //! REISERFS
    const long FS_MAGIC_REISERFS = 0x52654973;
    
    //! CRAMFS
    const long FS_MAGIC_CRAMFS = 0x28cd3d45;
    
    //! ROMFS
    const long FS_MAGIC_ROMFS = 0x7275;
    
    //! RAMFS
    const long FS_MAGIC_RAMFS = 0x858458f6;
    
    //! SQUASHFS
    const long FS_MAGIC_SQUASHFS = 0x73717368;
    
    //! SYSFS
    const long FS_MAGIC_SYSFS = 0x62656572;
#endif

    //! Get human readable name of the filesystem.
    String GetFsHumanName(long fs_type);
} // namespace hwdet

#endif

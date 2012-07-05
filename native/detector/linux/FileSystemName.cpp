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

/*! \file FileSystemName.cpp
 *  \author Branislav Repcek
 *  \date 12. 12. 2006
 * 
 *  \brief Filesystem names.
 * 
 *  \note Original credits due to Michael Meskes, author of the stat from the coreutils.
 */

#include <sys/vfs.h>
#include "../common/UnicodeString.h"
#include "FileSystemName.h"

namespace hwdet
{
    /*! Return human-readable name of the filesystem with given id.
     * 
     *  \param fs_type Type of the filesystem from the statfs structure (f_type field).
     * 
     *  \return String with name of the filesystem.
     * 
     *  \note This method is slightly rewritten function human_fstype from the source 
     *        of the <code>coreutils</code>' <code>stat</code>. Original credits due to
     *        Michael Meskes.
     */
    String GetFsHumanName(long fs_type)
    {
        switch (fs_type)
        {
#if defined __linux__
            case FS_MAGIC_AFFS:
                return "affs";

            case FS_MAGIC_DEVPTS:
                return "devpts";
                
            case FS_MAGIC_EXT:
                return "ext";
                
            case FS_MAGIC_EXT2_OLD:
                return "ext2";
                
            case FS_MAGIC_EXT2:
                return "ext2/ext3";
                
            case FS_MAGIC_JFS:
                return "jfs";
                
            case FS_MAGIC_XFS:
                return "xfs";
                
            case FS_MAGIC_HPFS:
                return "hpfs";
                
            case FS_MAGIC_ISOFS:
                return "isofs";
                
            case FS_MAGIC_ISOFS_WIN:
                return "isofs";
                
            case FS_MAGIC_ISOFS_R_WIN:
                return "isofs";
                
            case FS_MAGIC_MINIX:
                return "minix";
                
            case FS_MAGIC_MINIX_30:
                return "minix (30 char.)";
                
            case FS_MAGIC_MINIX_V2:
                return "minix v2";
                
            case FS_MAGIC_MINIX_V2_30:
                return "minix v2 (30 char.)";
                
            case FS_MAGIC_MSDOS:
                return "msdos";
                
            case FS_MAGIC_FAT:
                return "fat";
                
            case FS_MAGIC_NCP:
                return "novell";
                
            case FS_MAGIC_NFS:
                return "nfs";
                
            case FS_MAGIC_PROC:
                return "proc";
                
            case FS_MAGIC_SMB:
                return "smb";
                
            case FS_MAGIC_XENIX:
                return "xenix";
                
            case FS_MAGIC_SYSV4:
                return "sysv4";
                
            case FS_MAGIC_SYSV2:
                return "sysv2";
                
            case FS_MAGIC_COH:
                return "coh";
                
            case FS_MAGIC_UFS:
                return "ufs";
                
            case FS_MAGIC_XIAFS:
                return "xia";
                
            case FS_MAGIC_NTFS:
                return "ntfs";
                
            case FS_MAGIC_TMPFS:
                return "tmpfs";
                
            case FS_MAGIC_REISERFS:
                return "reiserfs";
                
            case FS_MAGIC_CRAMFS:
                return "cramfs";
                
            case FS_MAGIC_ROMFS:
                return "romfs";
                
            case FS_MAGIC_RAMFS:
                return "ramfs";
                
            case FS_MAGIC_SQUASHFS:
                return "squashfs";
                
            case FS_MAGIC_SYSFS:
                return "sysfs";
#elif __GNU__
            case FSTYPE_UFS:
                return "ufs";
                
            case FSTYPE_NFS:
                return "nfs";
                
            case FSTYPE_GFS:
                return "gfs";
                
            case FSTYPE_LFS:
                return "lfs";
                
            case FSTYPE_SYSV:
                return "sysv";
                
            case FSTYPE_FTP:
                return "ftp";
                
            case FSTYPE_TAR:
                return "tar";
                
            case FSTYPE_AR:
                return "ar";
                
            case FSTYPE_CPIO:
                return "cpio";
                
            case FSTYPE_MSLOSS:
                return "msloss";
                
            case FSTYPE_CPM:
                return "cpm";
                
            case FSTYPE_HFS:
                return "hfs";
                
            case FSTYPE_DTFS:
                return "dtfs";
                
            case FSTYPE_GRFS:
                return "grfs";
                
            case FSTYPE_TERM:
                return "term";
                
            case FSTYPE_DEV:
                return "dev";
                
            case FSTYPE_PROC:
                return "proc";
                
            case FSTYPE_IFSOCK:
                return "ifsock";
                
            case FSTYPE_AFS:
                return "afs";
                
            case FSTYPE_DFS:
                return "dfs";
                
            case FSTYPE_PROC9:
                return "proc9";
                
            case FSTYPE_SOCKET:
                return "socket";
                
            case FSTYPE_MISC:
                return "misc";
                
            case FSTYPE_EXT2FS:
                return "ext2/ext3";
                
            case FSTYPE_HTTP:
                return "http";
                
            case FSTYPE_MEMFS:
                return "memfs";
                
            case FSTYPE_ISO9660:
                return "iso9660";
#endif
            default:
        }
        
        return "(unknown)";
    }
} // namespaspace hwdet

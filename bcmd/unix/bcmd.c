#define _XOPEN_SOURCE 700
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <netdb.h>
#include <getopt.h>
#include <netinet/in.h>
#include <errno.h>
#include <signal.h>
#include <fcntl.h>
#include <poll.h>

#include "bcmd.h"

/* Constants and Types ****************************************************************************/

/** The zero character. */
static const char
CZERO = '\0';

/** Number of the TCP protocol. */
static const int
PROTO_TCP = 6;

/** Number of miliseconds to wait for a connection. */
static const int
CONN_TIMEOUT = 5000;

/** Size of the data transfer buffer. */
static const size_t
BUF_SIZE = 3000;

/** An unusual number. */
static const int
ONE = 1;

/** An important error code. */
static const int
MINUS_ONE = -1;

/** A special string value. */
static const char * const
EMPTY = "";

/** The default port to use with BEEN. */
static const char * const
BEEN_PORT = "2336";

/** Array of long option definitions required by the getopt_long() library function. */
static const struct option
OPTIONS[] = {
	{
		.name = "host",
		.has_arg = required_argument,
		.flag = NULL,
		.val = 'h'
	},
	{
		.name = "port",
		.has_arg = required_argument,
		.flag = NULL,
		.val = 'p'
	},
	{
		.name = "input",
		.has_arg = required_argument,
		.flag = NULL,
		.val = 'i'
	},
	{
		.name = "blob",
		.has_arg = required_argument,
		.flag = NULL,
		.val = 'b'
	},
	{
		.name = NULL,
		.has_arg = 0,
		.flag = NULL,
		.val = 0
	}
};

/** Array of message texts, intended for future extensions. */
static const struct message {
		const char * const msg_text;
} messages[] = {
	{ .msg_text = NULL },																			//  0
	{ .msg_text = NULL },																			//  1
	{ .msg_text = "local: Signal setup failed" },													//  2
	{ .msg_text = "local: Duplicate option --input." },												//  3
	{ .msg_text = "local: Duplicate option --blob." },												//  4
	{ .msg_text = "local: Duplicate option --host." },												//  5
	{ .msg_text = "local: Duplicate option --port." },												//  6
	{ .msg_text = "local: Missing argument at option" },											//  7
	{ .msg_text = "local: Unknown option" },														//  8
	{ .msg_text = "local: Illegal option" },														//  9
	{ .msg_text = "local: Option contract broken" },												// 10
	{ .msg_text = "local: Invalid host info" },														// 11
	{ .msg_text = "local: Failed to open input file" },												// 12
	{ .msg_text = "local: Read error" },															// 13
	{ .msg_text = "local: Connection error" },														// 14
	{ .msg_text = "local: Reception error" },														// 15
	{ .msg_text = "local: Transmission error" },													// 16
	{ .msg_text = "local: Illegal zero byte" },														// 17
	{ .msg_text = "local: Unknown error" },															// 18
};

/** Enum of message indices. Do not forget to modify this enum when adding messages. */
enum result_enum {
	SUCCESS = 0,																					//  0
	HELP,																							//  1
	E_SIG_ERR,																						//  2
	E_DUP_INPUT,																					//  3
	E_DUP_BLOB,																						//  4
	E_DUP_HOST,																						//  5
	E_DUP_PORT,																						//  6
	E_MISSING_ARG,																					//  7
	E_UNKNOWN_OPT,																					//  8
	E_ILLEGAL_OPT,																					//  9
	E_OPT_CONTRACT,																					// 10
	E_INVALID_HOST,																					// 11
	E_INVALID_FILE,																					// 12
	E_READ_ERR,																						// 13
	E_CONN_ERR,																						// 14
	E_RECV_ERR,																						// 15
	E_TRAN_ERR,																						// 16
	E_ILLEGAL_ZERO,																					// 17
	E_UNKNOWN_ERR,																					// 18
	E_ARRAY_LENGTH																					// Keep this last.
};

/** Enum of possible escape sequence codes. */
enum esc_seq {
	SWITCH_STDOUT = 0,
	SWITCH_STDERR
};

/** Return values of auxiliary functions. */
typedef enum result_enum result_idx;

/** Return value of functions that read escape sequences. */
typedef enum esc_seq esc_idx;

/** Represents source of data to read from. */
union data_source {

	/** Pointer to the command line option to start with. */
	char ** d_argv;

	/** A file descriptor. */
	int d_fd;
};

/**
 * An abstract method for writing the output stream.
 *
 * @param out_fd Output stream file descriptor.
 * @param data_src Data source identification. Either argv position or a file descriptor.
 * @return SUCCESS or other (error) codes from result_enum.
 */
typedef result_idx out_writer( int out_fd, union data_source data_src );

/** A structure that holds command line options. */
struct opt_data {
		/** Pointer to argc. (Can be modified.) */
		int * const opt_argc;

		/** Pointer to argv. (Can be modified.) */
		char *** const opt_argv;

		/** Host name. (Should point to a command line parameter. */
		const char * opt_host;

		/** Host port in string decimal representation. getaddrinfo() likes that. */
		const char * opt_port;

		/** File name of the file to read. The value of EMPTY stands for standard input. */
		const char * opt_file;

		/** File name of the blob to read. The value of EMPTY stands for standard input. */
		const char * opt_blob;

		/** Pointer to output stream writer function. */
		out_writer * opt_writer;
};

/* Forward Declarations ***************************************************************************/

static result_idx
argv_writer( int out_fd, union data_source in_argv );

static result_idx
file_writer( int out_fd, union data_source in_src );

static result_idx
blob_writer( int out_fd, int blob_src );

static result_idx
copy_file( int out_fd, int in_fd );

static result_idx
copy_file_unchecked( int out_fd, int in_fd );

static void
error_message( result_idx index, const char * perror_msg );

static result_idx
get_options( struct opt_data * data );

static result_idx
get_fds( const struct opt_data * data, union data_source * in_src, int * in_blob, int * out_fd );

static result_idx
read_response( int sock_fd, int * ret_code );

static result_idx
try_connect( const struct addrinfo * addrlist, int * sock_fd );

static result_idx
buf_write( int out_fd, const char * buf_cur, size_t size );

static result_idx
open_file( const char * path, int * fd );

static result_idx
set_sig( void );

/* Public Code ************************************************************************************/

int
main( int argc, char ** argv ) {
	struct opt_data data = {
		.opt_argc = &argc,
		.opt_argv = &argv,
	};
	union data_source args_source;
	int blob_source;
	int netconn;
	int ret_code;
	result_idx subres;
	result_idx newres;

	if ( ( subres = set_sig() ) ) {
		return subres;
	}

	switch ( subres = get_options( &data ) ) {
		case SUCCESS:
			break;
		case HELP:
			fputs(
				"BEEN command line wrapper\n"
				"\t-i --input\n"
				"\t\tDo not send command line arguments. Read from a file instead.\n"
				"\t\tReads standard input when either '-' or no attribute is given.\n"
				"\t-b --blob\n"
				"\t\tTransfer a blob together with the request. Reads from a file.\n"
				"\t\tReads standard input when either '-' or no attribute is given.\n"
				"\t-h --host\n"
				"\t\tName or IP address where the BEEN command line service is listening.\n"
				"\t\tIf not set, then BEEN_HOST environment variable will be used.\n"
				"\t\tWhen BEEN_HOST is not set, localhost will be contacted.\n"
				"\t-p --port\n"
				"\t\tPort number where the BEEN command line service is listening.\n"
				"\t\tIf not set, then BEEN_PORT environment variable will be used.\n"
				"\t\tWhen BEEN_PORT is not set, port number 2336 will be used.\n",
				stderr
			);
			return SUCCESS;
			break;
		default:
			return subres;
			break;
	}

	if ( ( subres = get_fds( &data, &args_source, &blob_source, &netconn ) ) ) {
		return subres;
	}

	if ( ( subres = data.opt_writer( netconn, args_source ) ) ) {
																									// No action, will read anyway.
	} else if ( data.opt_blob ) {																	// Send blob on success.
		subres = blob_writer( netconn, blob_source );
	}

	if ( ( newres = shutdown( netconn, SHUT_WR ) ) ) {												// There may still be sth to read!
		if ( !subres ) { subres = newres; }															// Prefer earlier errors.
		error_message( E_TRAN_ERR, NULL );															// Report this anyway.
	}

	if ( data.opt_file && EMPTY != data.opt_file ) {												// Read from a file? Close it!
		close( args_source.d_fd );																	// Errors are irrelevant here.
	}

	if ( data.opt_blob && EMPTY != data.opt_blob ) {												// Sent a blob? Close it!
		close( blob_source );																		// Errors are irrelevant here.
	}

	ret_code = 0;
	newres = read_response( netconn, &ret_code );													// Now read the response.
	if ( !subres ) { subres = newres; }																// Prefer earlier errors.
	close( netconn );																				// Close the socket.

	return ret_code ? ret_code : subres;															// Prefer remote errors over local.
}

/* Auxiliary Code *********************************************************************************/

/**
 * This is an implementation of out_writer. Data is read from command line arguments.
 * Transmits command line arguments separated by spaces. Spaces inside the command line
 * arguments cannot be distinguished from the separator spaces. This could be changed
 * by simply introducing an escape character, but there seems to be no need to do that.
 *
 * @param out_fd Output file descriptor.
 * @param in_argv Pointer to the first argument to transmit.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
argv_writer( int out_fd, union data_source in_argv ) {
	char buffer[ BUF_SIZE ];
	char * const * cur_argv;
	const char * cur_arg;
	const char * const end_buf = buffer + BUF_SIZE;
	char * cur_buf;
	char cur_char;
	result_idx subres;

	cur_buf = buffer;
	cur_argv = in_argv.d_argv;

	if ( ( cur_arg = *cur_argv ) ) {																// Do we have any args?
		for ( ; ( cur_char = *cur_arg ); ++cur_arg ) {												// Sending the first arg.
			*cur_buf++ = cur_char;																	// Copy one character.
			if ( cur_buf == end_buf ) {																// The buffer is full.
				if ( ( subres = buf_write( out_fd, buffer, BUF_SIZE ) ) ) {							// Flush failed for some reason.
					return subres;
				}
				cur_buf = buffer;																	// Buffer flushed.
			}
		}

		for ( ++cur_argv; ( cur_arg = *cur_argv ); ++cur_argv ) {									// Send further args if present.
			*cur_buf++ = ' ';																		// Adding a blank. (Buffer OK.)
			if ( cur_buf == end_buf ) {																// The buffer is full.
				if ( ( subres = buf_write( out_fd, buffer, BUF_SIZE ) ) ) {							// Flush failed for some reason.
					return subres;
				}
				cur_buf = buffer;																	// Buffer flushed.
			}

			for ( ; ( cur_char = *cur_arg ); ++cur_arg ) {											// Sending one argument.
				*cur_buf++ = cur_char;																// Copy one character.
				if ( cur_buf == end_buf ) {															// The buffer is full.
					if ( ( subres = buf_write( out_fd, buffer, BUF_SIZE ) ) ) {						// Flush failed for some reason.
						return subres;
					}
					cur_buf = buffer;																// Buffer flushed.
				}
			}
		}

		return buf_write( out_fd, buffer, cur_buf - buffer );										// A final buffer flush.
	} else {
		return SUCCESS;																				// Nothing to write.
	}
}

/**
 * This is an implementation of out_writer. Data is read from a file.
 *
 * @param out_fd Output file descriptor.
 * @param in_fd The file descriptor to read from.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
file_writer( int out_fd, union data_source in_src ) {
	return copy_file( out_fd, in_src.d_fd );
}

/**
 * Writes a blob from the requested file with a leading zero byte.
 *
 * @param out_fd Output file descriptor.
 * @param blob_src The file descriptor to read from.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
blob_writer( int out_fd, int blob_src ) {
	result_idx result;

	if ( ( result = buf_write( out_fd, &CZERO, ONE ) ) ) {
		return result;
	}
	return copy_file_unchecked( out_fd, blob_src );
}

/**
 * Just a buffered file replicator. Performs null byte checks to avoid false blobs.
 * TODO: Escape sequences could be used so that options can contain null bytes. Low priority.
 *
 * @param out_fd Output file.
 * @param in_fd Input file.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
copy_file( int out_fd, int in_fd ) {
	char buffer[ BUF_SIZE ];
	char * cur, * end;
	ssize_t bread;
	result_idx result;

	while ( ( bread = read( in_fd, buffer, BUF_SIZE ) ) > 0 ) {
		end = buffer + bread;
		for ( cur = buffer; cur < end; ++cur ) {
			if ( !*cur ) {
				*cur = ' ';
				error_message( E_ILLEGAL_ZERO, "Replaced with space" );
			}
		}
		if ( ( result = buf_write( out_fd, buffer, bread ) ) ) {
			return result;
		}
	}

	if ( MINUS_ONE == bread ) {
		error_message( E_READ_ERR, NULL );
		return E_READ_ERR;
	}

	return SUCCESS;
}

/**
 * Another buffered file replicator, this time without null byte checks.
 *
 * @param out_fd Output file.
 * @param in_fd Input file.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
copy_file_unchecked( int out_fd, int in_fd ) {
	char buffer[ BUF_SIZE ];
	ssize_t bread;
	result_idx result;

	while ( ( bread = read( in_fd, buffer, BUF_SIZE ) ) > 0 ) {
		if ( ( result = buf_write( out_fd, buffer, bread ) ) ) {
			return result;
		}
	}

	if ( MINUS_ONE == bread ) {
		error_message( E_READ_ERR, NULL );
		return E_READ_ERR;
	}

	return SUCCESS;
}

/**
 * Writes a message to the standard error output.
 *
 * @param index Index of the message to report. See messages[] for details.
 * @param perror_msg Further details for the error message. When NULL, the standard perror()
 * mechanism will be used. When EMPTY, only the corresponding item of messages[] will be written.
 * Other pointers result in a behavior similar to perror(), but the order of buit-in and custom
 * messages is reversed. (Surprisingly, this behavior does make sense...)
 */
static void
error_message( result_idx index, const char * error_msg ) {
	if ( error_msg ) {
		if ( EMPTY == error_msg ) {
			fputs( messages[ index ].msg_text, stderr );
			fputc( '\n', stderr );
		} else {
			fprintf( stderr, "%s: %s\n", messages[ index ].msg_text, error_msg );
		}
	} else {
		perror( messages[ index ].msg_text );
	}
	fflush( stderr );
}

/**
 * Reads command line arguments and gathers data obtained from them. Uses GNU long options, too.
 *
 * @param data Pointer to a structure where command line data will be stored.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
get_options( struct opt_data * data ) {
	enum {
		HOST = 1u,
		INPUT = 1u << 1u,
		BLOB = 1u << 2u,
		PORT = 1u << 3u
	};
	char * envdata;
	unsigned int seen;
	int	cur_opt;
	result_idx result;

	if ( *data->opt_argc > 1 ) {
		if ( !strcmp( "--help", ( *data->opt_argv )[ 1 ] ) ) {
			return HELP;																			// A shortcut. No checks after help.
		}
	}

	data->opt_file = NULL;
	data->opt_blob = NULL;
	data->opt_host = NULL;																			// This means localhost.
	data->opt_port = BEEN_PORT;
	data->opt_writer = argv_writer;
	opterr = 0;
	result = SUCCESS;
	seen = 0;

	if ( ( envdata = getenv( "BEEN_HOST" ) ) ) {
		data->opt_host = envdata;
	}
	if ( ( envdata = getenv( "BEEN_PORT" ) ) ) {
		data->opt_port = envdata;
	}

	while (
		MINUS_ONE !=
		( cur_opt = getopt_long( *data->opt_argc, *data->opt_argv, ":b:h:i:p:", OPTIONS, NULL ) )
	) {
		switch ( cur_opt ) {
			case 'i':
				if ( INPUT & seen ) {
					error_message( E_DUP_INPUT, EMPTY );
					if ( !result ) { result = E_DUP_INPUT; }
				} else {
					seen |= INPUT;
					if ( *optarg == '-' && optarg[ 1 ] == '\0' ) {
						data->opt_file = EMPTY;														// This means standard input.
					} else {
						data->opt_file = optarg;
					}
					data->opt_writer = file_writer;
				}
				break;
			case 'b':
				if ( BLOB & seen ) {
					error_message( E_DUP_BLOB, EMPTY );
					if ( !result ) { result = E_DUP_BLOB; }
				} else {
					seen |= BLOB;
					if ( *optarg == '-' && optarg[ 1 ] == '\0' ) {
						data->opt_blob = EMPTY;
					} else {
						data->opt_blob = optarg;
					}
				}
				break;
			case 'h':
				if ( HOST & seen ) {
					error_message( E_DUP_HOST, EMPTY );
					if ( !result ) { result = E_DUP_HOST; }
				} else {
					seen |= HOST;
					data->opt_host = optarg;
				}
				break;
			case 'p':
				if ( PORT & seen ) {
					error_message( E_DUP_PORT, EMPTY );
					if ( !result ) { result = E_DUP_PORT; }
				} else {
					seen |= PORT;
					data->opt_port = optarg;
				}
				break;
			case ':':
				switch ( optopt ) {
					case 'i':
						if ( INPUT & seen ) {
							error_message( E_DUP_INPUT, EMPTY );
							if ( !result ) { result = E_DUP_INPUT; }
						} else {
							seen |= INPUT;
							data->opt_file = EMPTY;
							data->opt_writer = file_writer;
						}
						break;
					case 'b':
						if ( BLOB & seen ) {
							error_message( E_DUP_BLOB, EMPTY );
							if ( !result ) { result = E_DUP_BLOB; }
						} else {
							seen |= BLOB;
							data->opt_blob = EMPTY;
						}
						break;
					default:
						error_message( E_MISSING_ARG, ( *data->opt_argv )[ optind - 1 ] );
						if ( !result ) { result = E_MISSING_ARG; }
						break;
				}
				break;
			case '?':
				error_message( E_UNKNOWN_OPT, ( *data->opt_argv )[ optind - 1 ] );
				if ( !result ) { result = E_UNKNOWN_OPT; }
				break;
			default:
				error_message( E_UNKNOWN_ERR, "Something unknown has happened." );
				if ( !result ) { result = E_UNKNOWN_ERR; }
				break;
		}
	}

	*data->opt_argc -= optind;
	*data->opt_argv += optind;

	if ( INPUT & seen && **data->opt_argv ) {
		error_message( E_ILLEGAL_OPT, **data->opt_argv );
		if ( !result ) { result = E_ILLEGAL_OPT; }
	}

	if ( EMPTY == data->opt_file && EMPTY == data->opt_blob ) {										// Both reading stdin?
		error_message( E_OPT_CONTRACT, "Options and blob both set to stdin." );
		if ( !result ) { result = E_ILLEGAL_OPT; }
	}

	return result;
}

/**
 * Opens the output file descriptor (a network connection). If input from an external file
 * is required, opens the file and returns its descriptor, too.
 *
 * @param data The data structure with connection and input/output parameters.
 * @param in_src Source of the data to read from. Either a file descriptor or an argv pointer.
 * @param out_fd Output file descriptor. This is a network socket.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
get_fds( const struct opt_data * data, union data_source * in_src, int * in_blob, int * out_fd ) {
	const struct addrinfo hint_info = {
		.ai_flags = AI_V4MAPPED | AI_ALL | AI_NUMERICSERV /* | AI_CANONNAME */,
			.ai_family = AF_INET6,
			.ai_socktype = SOCK_STREAM,
			.ai_protocol = PROTO_TCP,
			.ai_addrlen = 0,
			.ai_addr = NULL,
			.ai_canonname = NULL,
			.ai_next = NULL
	};
	struct addrinfo * info;
	int	msg, check_stat;
	result_idx subres;

	check_stat = ONE;

	if ( data->opt_blob ) {
		if ( EMPTY == data->opt_blob ) {
			*in_blob = STDIN_FILENO;
			check_stat = 0;																			// One! empty, no need to check.
		} else {
			if ( ( subres = open_file( data->opt_blob, in_blob ) ) ) {
				return subres;
			}
		}
	} else {
		check_stat = 0;																				// No blob, no need to check.
	}

	if ( data->opt_file ) {																			// Read from a file?
		if ( EMPTY == data->opt_file ) {															// How about stdin?
			in_src->d_fd = STDIN_FILENO;
			check_stat = 0;																			// One! empty, no need to check.
		} else {																					// No, open a different file.
			if ( ( subres = open_file( data->opt_file, &in_src->d_fd ) ) ) {
				return subres;
			}
		}
	} else {																						// Nope. Read from argv.
		in_src->d_argv = *data->opt_argv;
		check_stat = 0;																				// Argv and file - no conflict.
	}

	if ( check_stat ) {
		struct stat file, blob;

		fstat( in_src->d_fd, &file );
		fstat( *in_blob, &blob );
		if ( file.st_ino == blob.st_ino ) {
			if ( data->opt_file ) { close( in_src->d_fd ); }
			if ( data->opt_blob ) { close( *in_blob ); }
			error_message( E_OPT_CONTRACT, "Options and blob both set to the same file." );
			return E_OPT_CONTRACT;
		}
	}

	if ( ( msg = getaddrinfo( data->opt_host, data->opt_port, &hint_info, &info ) ) ) {				// Resolve the host name.
		if ( data->opt_file ) { close( in_src->d_fd ); }
		if ( data->opt_blob ) { close( *in_blob ); }
		error_message( E_INVALID_HOST, gai_strerror( msg ) );
		return E_INVALID_HOST;
	}

	if ( try_connect( info, out_fd ) ) {															// Try to establish a connection.
		if ( data->opt_file ) { close( in_src->d_fd ); }
		if ( data->opt_blob ) { close( *in_blob ); }
		freeaddrinfo( info );
		return E_CONN_ERR;
	}

	freeaddrinfo( info );
	return SUCCESS;
}

/**
 * Reads response from the other side and writes it to standard (error) output.
 *
 * @param sock_fd The file descriptor to read from.
 * @param ret_code Pointer to where return code should be stored.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
read_response( int sock_fd, int * ret_code ) {
	enum {																							// How to interpret next byte.
			NORMAL = 0,
			ESCAPE
	} esc_state;

	int fd_mod;																						// Output descriptor switch.

	char buf_in[ BUF_SIZE ];																		// Input buffer.
	char buf_out[ BUF_SIZE ];																		// Output buffer.

	const char * end_buf_in;																		// Past end of input buffer.
	const char * const end_buf_out = buf_out + BUF_SIZE;											// Past end of output buffer.

	const char * cur_buf_in;																		// Input buffer position.
	char * cur_buf_out;																				// Output buffer position.

	ssize_t bread;																					// Bytes read from input.
	result_idx subres;																				// Result of a write operation.
	int r_code;																						// The return code to set.
	char cur_char;																					// The current character.
	int cur_out_fd;																					// The current output stream.

	r_code = 0;																						// Default return code.
	cur_out_fd = STDOUT_FILENO;																		// STDOUT is the default.
	fd_mod = 0;																						// Descriptor not modified yet.
	esc_state = NORMAL;																				// No escape sequence yet.

	cur_buf_out = buf_out;																			// Writing from the beginning.
	while ( ( bread = read( sock_fd, buf_in, BUF_SIZE ) ) > 0 ) {									// Read from the input file.
		end_buf_in = buf_in + bread;																// Varying input buffer end...

		for ( cur_buf_in = buf_in; cur_buf_in < end_buf_in; ++cur_buf_in ) {						// Iterate over the input.
			if ( ( cur_char = *cur_buf_in ) ) {														// Get a byte from input.
				if ( esc_state ) {																	// Regular byte or escape sequence?
					switch ( cur_char ) {
						case '\1':																	// Switch to STDOUT.
							fd_mod = STDOUT_FILENO;
							break;
						case '\2':																	// Switch to STDERR.
							fd_mod = STDERR_FILENO;
							break;
						default:																	// Unknown? Copy it.
							r_code = (unsigned char) cur_char;										// This sets the return code.
							break;
					}
					esc_state = NORMAL;																// Restore escape state
				} else {																			// Just a regular byte. Copy it.
					*cur_buf_out++ = cur_char;
				}
			} else {																				// A special byte seen: ZERO.
				if ( esc_state ) {
					*cur_buf_out++ = '\0';															// Zero itself. \0\0 -> \0.
					esc_state = NORMAL;
				} else {
					esc_state = ESCAPE;																// Set the escape state flag.
				}
			}

			if ( cur_buf_out == end_buf_out ) {														// Output buffer is full.
				if ( ( subres = buf_write( cur_out_fd, buf_out, BUF_SIZE ) ) ) {					// Flush and report the result.
					return subres;																	// Something failed.
				}
				cur_buf_out = buf_out;
			}

			if ( fd_mod ) {
				if ( ( subres = buf_write( cur_out_fd, buf_out, cur_buf_out - buf_out ) ) ) {		// Flush and report the result.
					return subres;																	// Something failed.
				}
				cur_buf_out = buf_out;																// Restore buffer position.
				cur_out_fd = fd_mod;
				fd_mod = 0;
			}
		}
	}

	if ( MINUS_ONE == bread ) {
		if ( cur_buf_out != buf_out ) {																// Write what you can...
			buf_write( cur_out_fd, buf_out, cur_buf_out - buf_out );								// ...and ignore errors.
		}
		error_message( E_RECV_ERR, NULL );
		return E_RECV_ERR;
	}

	*ret_code = r_code;
	return buf_write( cur_out_fd, buf_out, cur_buf_out - buf_out );									// Write the last piece of data.
}

/**
 * Tries to establish a network connection based on the supplied struct addrinfo.
 * Uses a nonblocking socket and poll() to enforce a connection timeout.
 *
 * @param addrlist A linked list of struct addrinfo generated by getaddrinfo().
 * @param sock_fd Pointer to where the new file descriptor should be stored.
 * @return SUCCESS or an error code when appropriate.
 */
static result_idx
try_connect( const struct addrinfo * addrlist, int * sock_fd ) {
	const struct linger lgr_nowait = {
		.l_onoff = 1,
		.l_linger = 0
	};
	const struct linger lgr_normal = {
		.l_onoff = 1,
		.l_linger = CONN_TIMEOUT
	};
	const struct addrinfo * cur_info;
	struct pollfd pfd = {
		.events = POLLOUT
	};
	int sock, errcode;
	socklen_t errlen;

	errlen = sizeof( int );
	for ( cur_info = addrlist; cur_info; cur_info = cur_info->ai_next ) {							// Try all addresses we have.
		if (
			MINUS_ONE == (
			sock = socket(
				cur_info->ai_family,
				cur_info->ai_socktype,
				cur_info->ai_protocol
			) )
		) {																							// Fatal error.
			error_message( E_UNKNOWN_ERR, NULL );
			return E_UNKNOWN_ERR;
		} else {																					// OK, got a socket.
			fcntl( sock, F_SETFL, O_NONBLOCK );														// Set nonblocking.
			setsockopt( sock, SOL_SOCKET, SO_LINGER, &lgr_nowait, sizeof( struct linger ) );		// Set linger timer.
			if ( connect( sock, cur_info->ai_addr, cur_info->ai_addrlen ) ) {						// Try to connect.
				if ( errno == EINPROGRESS ) {														// Not connected yet, but OK.
					pfd.fd = sock;
					if ( poll( &pfd, 1, CONN_TIMEOUT ) ) {											// Wait till connection is available.
						getsockopt( sock, SOL_SOCKET, SO_ERROR, &errcode, &errlen );				// Get error status.
						if ( errcode ) {															// Something bad happened.
							error_message( E_CONN_ERR, strerror( errcode ) );
						} else {																	// Connection established.
							break;
						}
					} else {																		// The connection failed.
						error_message( E_CONN_ERR, "Connection timed out." );
					}
				} else {
					error_message( E_CONN_ERR, NULL );
				}
			} else {																				// Connected immediately.
				break;
			}
		}
		close( sock );																				// Close that thing.
	}

	if ( cur_info ) {																				// We have a connection.
		setsockopt( sock, SOL_SOCKET, SO_LINGER, &lgr_normal, sizeof( struct linger ) );			// Disable linger timer.
		fcntl( sock, F_SETFL, 0 );																	// Unset nonblocking flag.
		*sock_fd = sock;
		return SUCCESS;
	} else {																						// No success.
		return E_CONN_ERR;
	}
}

/**
 * Writes contents of a buffer to an output stream. Attempts to resume the operation when
 * interrupted by a signal or (more generally) when a short write occurs. This probably
 * does not handle all the possible situations and should be changed later.
 *
 * @param out_fd Output file descriptor.
 * @param buf_cur Pointer to the first byte of the buffer.
 * @param size Number of bytes to be written.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
buf_write( int out_fd, const char * buf_cur, size_t size ) {
	ssize_t written;

	while ( size ) {																				// write() may be interrupted.
		written = write( out_fd, buf_cur, size );
		if ( MINUS_ONE == written ) {
			error_message( E_TRAN_ERR, NULL );
			return E_TRAN_ERR;
		}
		buf_cur += written;
		size -= written;
	}
	return SUCCESS;
}

/**
 * Attempts to open a file and attains an advisory read lock on it.
 * There is no timeout. It may wait forever. (This should be changed...)
 *
 * @param path Path to the file to be locked.
 * @param fd Pointer to where the new file descriptor should be stored.
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
open_file( const char * path, int * fd ) {
	const struct flock lock = {
		.l_type = F_RDLCK,
		.l_whence = SEEK_SET,
		.l_start = 0,
		.l_len = 0
	};
	int file;

	if ( MINUS_ONE == ( file = open( path, O_RDONLY ) ) ) {
		error_message( E_INVALID_FILE, NULL );
		return E_INVALID_FILE;
	} else {
		while ( fcntl( file, F_SETLKW, &lock ) );													// Waiting can be interrupted.
		*fd = file;
		return SUCCESS;
	}
}

/**
 * Sets SIGPIPE to be ignored. Sets the SA_RESTART flag on it.
 * Furthermore, the SA_RESTART flag is set on all signals that are normally ignored by default.
 * This is not necessary and will be removed later. Only SIGPIPE matters.
 *
 * @return SUCCESS or an error code from result_enum when appropriate.
 */
static result_idx
set_sig( void ) {

	#ifdef __APPLE__
		// SIGPWR and SIGWINCH not defined on MAC OS X
		const int signals[] = { SIGURG, SIGCHLD };													// List of ignored signals.
	#else
    	const int signals[] = { SIGPWR, SIGWINCH, SIGURG, SIGCHLD };								// List of ignored signals.
	#endif

	const int * const end_sig = signals + sizeof( signals ) / sizeof( int );
	const int * cur_sig;
	struct sigaction action;

	action.sa_handler = SIG_IGN;
	action.sa_flags = SA_RESTART;
	sigemptyset( &action.sa_mask );

	if ( sigaction( SIGPIPE, &action, NULL ) ) {													// Set SA_RESTART on SIGPIPE.
		error_message( E_SIG_ERR, NULL );
		return E_SIG_ERR;
	}

	for ( cur_sig = signals; cur_sig < end_sig; ++cur_sig ) {										// Set SA_RESTART on ignored signals.
		if ( sigaction( *cur_sig, NULL, &action ) ) {
			error_message( E_SIG_ERR, NULL );
			return E_SIG_ERR;
		}
		action.sa_handler = SIG_IGN;																// This is probably not needed.
		action.sa_flags = SA_RESTART;
		if ( sigaction( *cur_sig, &action, NULL ) ) {
			error_message( E_SIG_ERR, NULL );
			return E_SIG_ERR;
		}
	}

	return SUCCESS;
}

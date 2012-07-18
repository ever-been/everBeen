#ifndef BCMD_H_
#define BCMD_H_

/** Whether to output option error messages. */
extern int
opterr;

/** Index of the next command line option. */
extern int
optind;

/** Option character for error reporting. */
extern int
optopt;

/** Argument of the last option found. */
extern char	*
optarg;

#endif /* BCMD_H_ */

/**
 * The bulk of <em>Object Repository</em> is implemented in this package. The main class is obviously {@link cz.cuni.mff.d3s.been.objectrepository.ObjectRepository}, which implements the EverBEEN service and starts all additional sub-services and threads.
 *
 * The {@link cz.cuni.mff.d3s.been.objectrepository.QueueDrain} and its derivates handle the persist request and query queue pickup.
 *
 * The query handling mechanism is mainly implemented in {@link cz.cuni.mff.d3s.been.objectrepository.AnswerQueryAction}.
 */
package cz.cuni.mff.d3s.been.objectrepository;
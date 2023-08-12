/** The Task type enumerate, added onto server's task queue to process by different roles. */
public enum TaskType {
  /** Write task to database server. */
  Write,
  /** Request task type. */
  Request,
  /** Promise task type. */
  Promise,
  /** Accepted task type. */
  Accepted,
  /** Prepare task type. */
  Prepare,
  /** Accept task type. */
  Accept,
  /** Announce task to learner. */
  Announce,
}

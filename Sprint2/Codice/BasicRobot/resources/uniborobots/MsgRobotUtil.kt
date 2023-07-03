package uniborobots

object MsgRobotUtil {
	
	
	val forwardMsg   = "{\"robotmove\":\"moveForward\", \"time\": 400}"
	val backwardMsg  = "{\"robotmove\":\"moveBackward\", \"time\": 400}"
	val turnLeftMsg  = "{\"robotmove\":\"turnLeft\", \"time\": 300}"
	val turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}"
	val haltMsg      = "{\"robotmove\":\"alarm\", \"time\": 10}"
	
	val wMsg  = "w"
	val lMsg  = "l"
	val rMsg  = "r"
	val sMsg  = "s"
	val hMsg  = "h"
}

import org.json.JSONObject
import java.io.File

object DomainSystemConfig {

	private var TicketTime : Long = 0;
    private var Maxweight : Long = 0;


    init {
        try {
            val config = File("AppConfig.json").readText(Charsets.UTF_8)
            val jsonObject   =  JSONObject( config );

            TicketTime= jsonObject.getLong("TicketTime")
            Maxweight= jsonObject.getLong("Maxweight")

        } catch (e : Exception) {
            println(" ${this.javaClass.name}  | ${e.localizedMessage}, activate simulation by default")
        }
    }

    fun getTicketTime() : Long {
        return TicketTime;
    }

    fun getMaxWeight() : Long {
        return Maxweight;
    }

}

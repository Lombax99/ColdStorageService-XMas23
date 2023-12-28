

import java.io.BufferedReader

import java.io.InputStreamReader

object SonarService {

    var reader: BufferedReader? = null


    init {
        try {

            var p  = Runtime.getRuntime().exec("python3 -u sonar.py")
            reader = BufferedReader( InputStreamReader(p.inputStream))
        } catch (e : Exception) {
            println(e.message)
        }
    }

    fun getDistance() : Double {
        var distance = 100.0;
        try{
            distance = reader!!.readLine().toDouble()
        } catch( e : Exception){
            println(e.message)
        }
        println(distance)
        return distance
    }


}

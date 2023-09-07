package robotVirtual;

public interface IVrobotMoves {
    public boolean step(long time) throws Exception;
    public void turnLeft() throws Exception;
    public void turnRight() throws Exception;
    public void forward( int time ) throws Exception;
    public void backward( int time ) throws Exception;
    public void move(String cmd ) throws Exception;
    public void halt() throws Exception;
}

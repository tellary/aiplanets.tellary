/**
 * Created by Silvestrov Ilya
 * Date: Oct 20, 2010
 * Time: 11:14:01 PM
 */
public class DefaultTimer implements Timer {
    @Override
    public boolean shouldStop() {
        //noinspection SimplifiableIfStatement
        if ("true".equals(System.getProperty("debug"))) {
            return false;
        }
        boolean shouldExit = System.currentTimeMillis() - MyBot.start > MyBot.TIMESTOP;
        if (shouldExit) {
            Log.log(MyBot.turn, "Exiting by timeout");
        }
        return shouldExit;
    }
}

public class ProspectorBot {
    public static void DoTurn(PlanetWars pw) {
	// (1) If we current have a fleet in flight, just do nothing.
	if (pw.myFleets().size() >= 1) {
	    return;
	}
	// (2) Find my strongest planet.
	Planet source = null;
	double sourceScore = Double.MIN_VALUE;
	for (Planet p : pw.myPlanets()) {
	    double score = (double)p.numShips() / (1 + p.GrowthRate());
	    if (score > sourceScore) {
		sourceScore = score;
		source = p;
	    }
	}
	// (3) Find the weakest enemy or neutral planet.
	Planet dest = null;
	double destScore = Double.MIN_VALUE;
	for (Planet p : pw.NotMyPlanets()) {
	    double score = (double)(1 + p.GrowthRate()) / p.numShips();
	    if (score > destScore) {
		destScore = score;
		dest = p;
	    }
	}
	// (4) Send half the ships from my strongest planet to the weakest
	// planet that I do not own.
	if (source != null && dest != null) {
	    int numShips = source.numShips() / 2;
	    pw.IssueOrder(source, dest, numShips);
	}
    }

    public static void main(String[] args) {
	String line = "";
	String message = "";
	int c;
	try {
	    while ((c = System.in.read()) >= 0) {
		switch (c) {
		case '\n':
		    if (line.equals("go")) {
			PlanetWars pw = new PlanetWars(message);
			DoTurn(pw);
		        pw.FinishTurn();
			message = "";
		    } else {
			message += line + "\n";
		    }
		    line = "";
		    break;
		default:
		    line += (char)c;
		    break;
		}
	    }
	} catch (Exception e) {
	    // Owned.
	}
    }
}


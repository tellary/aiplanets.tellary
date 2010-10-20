import java.util.*;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 6, 2010
 * Time: 11:54:06 PM
 */
public class PlanSelection {
    @SuppressWarnings({"FieldCanBeLocal"})
    private int populationSize = 100;
    @SuppressWarnings({"FieldCanBeLocal"})
    private int topSize = 10;
    private int idx = 0;

    private static class ScoredPlan {
        Plan plan;
        int score;
        int idx;

        private ScoredPlan(Plan plan, int score, int idx) {
            this.plan = plan;
            this.score = score;
            this.idx = idx;
        }
    }

    private TreeSet<ScoredPlan> population = new TreeSet<ScoredPlan>(
            new Comparator<ScoredPlan>() {
                @Override
                public int compare(ScoredPlan o1, ScoredPlan o2) {
                    if (o1.score > o2.score) {
                        return -1;
                    } else if (o1.score == o2.score) {
                        return o1.idx < o2.idx ? -1 : o1.idx == o2.idx? 0 : 1;
                    } else {
                        return 1;
                    }
                }
            }
    );

    private Scorer scorer = new StateEvaluationScorer();

    public Plan getBestPlan() {
        return population.first().plan;
    }

    public void setScorer(Scorer scorer) {
        this.scorer = scorer;
    }

    public void doPlanSelection(PlanetWarsState initialState, Collection<Plan> planQueue) {
        Plan plan;

        while (!planQueue.isEmpty()) {
            if (shouldStop())
                return;
            Iterator<Plan> iter = planQueue.iterator();
            plan = iter.next();
            iter.remove();
            int score = scorer.score(initialState, plan);
            if (score == Integer.MIN_VALUE)
                continue;
            if (population.size() == populationSize) {
                if (score > population.last().score) {
                    population.pollLast();
                } else {
                    continue;
                }
            }
            if (!plan.isEmpty() && entersTop(score)) {
                for (ScoredPlan planToMergeWith : population) {
                    if (!planToMergeWith.plan.isEmpty())
                        planQueue.add(Plan.sum(plan, planToMergeWith.plan));
                }
            }

            population.add(new ScoredPlan(plan, score, idx));
            ++idx;
        }
    }

    private boolean entersTop(int score) {
        Iterator<ScoredPlan> topScoreIter = population.iterator();
        for (int i = 0; i < topSize; ++i) {
            if (!topScoreIter.hasNext()) {
                return true;
            }
            int topScore = topScoreIter.next().score;
            if (score > topScore) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldStop() {
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

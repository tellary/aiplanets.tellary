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

    private Timer timer = new DefaultTimer();

    public PlanSelection() {
    }

    public PlanSelection(Timer timer) {
        this.timer = timer;
    }

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
                        //o1.idx == o2.idx is never true
                        return o1.idx < o2.idx ? -1 : 1;
                    } else {
                        return 1;
                    }
                }
            }
    );

    private Scorer scorer = new StateEvaluationScorer();

    public Plan getBestPlan() {
        if (population.isEmpty()) {
            Plan plan = new Plan();
            plan.addTransitions(new SquareMatrix(0));
            return plan;
        }
        return population.first().plan;
    }

    public int getBestScore() {
        if (population.isEmpty())
            return Integer.MIN_VALUE;
        return population.first().score;
    }

    public void setScorer(Scorer scorer) {
        this.scorer = scorer;
    }

    public void doPlanSelection(PlanetWarsState initialState, Collection<Plan> planQueue) {
        Plan plan;

        while (!planQueue.isEmpty()) {
            if (timer.shouldStop())
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
}

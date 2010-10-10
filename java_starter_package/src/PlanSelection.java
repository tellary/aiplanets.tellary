import java.util.*;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 6, 2010
 * Time: 11:54:06 PM
 */
public class PlanSelection {
    private int populationSize = 100;
    private int topSize = 10;
    int idx = 0;

    private static class ScoredPlan {
        Plan plan;
        int score;
        int idx;

        private ScoredPlan(Plan plan, int score, int idx) {
            this.plan = plan;
            this.score = score;
            this.idx = idx;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ScoredPlan) {
                Plan plan = ((ScoredPlan)o).plan;
                return plan == this.plan;
            }
            return false;
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

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setTopSize(int topSize) {
        this.topSize = topSize;
    }

    public Plan getBestPlan() {
        return population.first().plan;
    }

    public int getBestScore() {
        return population.first().score;
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
                        planQueue.add(sumPlans(plan, planToMergeWith.plan));
                }
            }

            population.add(new ScoredPlan(plan, score, idx));
            ++idx;
        }
    }

    private static Plan sumPlans(Plan plan1, Plan plan2) {
        Iterator<SquareMatrix> plan1Iter = plan1.transitions().iterator();
        Iterator<SquareMatrix> plan2Iter = plan2.transitions().iterator();

        SquareMatrix tr, tr1, tr2;
        Plan sumPlan = new Plan();
        while (plan1Iter.hasNext() || plan2Iter.hasNext()) {
            if (plan1Iter.hasNext() && plan2Iter.hasNext()) {
                tr1 = plan1Iter.next();
                tr2 = plan2Iter.next();
                if (tr1.size() != tr2.size()) {
                    throw new RuntimeException("Fuck! Different transition matrix sizes!");
                }
                tr = new SquareMatrix(tr1.size());
                tr.add(tr1).add(tr2);
                sumPlan.addTransitions(tr);
            } else if (plan1Iter.hasNext()) {
                sumPlan.addTransitions(plan1Iter.next());
            } else if (plan2Iter.hasNext()) {
                sumPlan.addTransitions(plan2Iter.next());
            }
        }
        return sumPlan;
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

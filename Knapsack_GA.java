import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveAction;

public class Knapsack_GA1 {
    static List<Integer> value = new ArrayList<Integer>();
    static List<Integer> weight = new ArrayList<Integer>();

    static int[] readFile(String filename) {
        int n = 0, max_weight;
        int[] ans = new int[3];
        try {
            Scanner s = new Scanner(new FileReader(filename));
            n = s.nextInt();
            max_weight = s.nextInt();
            while (s.hasNext()) {
                value.add(s.nextInt());
                weight.add(s.nextInt());
            }
            s.close();
            System.out.println(n + " " + max_weight);
        } catch (Exception e) {
            System.out.println("Error can't open " + filename);
            max_weight = 0;
        }
        ans[0] = n;
        ans[1] = max_weight;
        return ans;
    }

    static List<List<Integer>> initialPopulation(List<List<Integer>> list, int r, int c) {
        Random random = new Random();
        int x = 0;
        for (int i = 0; i < r; i++) {
            list.add(new ArrayList<Integer>());
            for (int j = 0; j < c; j++) {
                x = random.nextInt(101);
                if (x > 90)
                    list.get(i).add(1);
                else
                    list.get(i).add(0);
            }
        }
        return list;
    }

    static int sumValue(List<List<Integer>> population, int i) {
        int sum = 0, cnt = 0;

        for (int j : population.get(i)) {
            sum += j * value.get(cnt++);
        }
        return sum;
    }

    static int sumWeight(List<List<Integer>> population, int i) {
        int sum = 0, cnt = 0;

        for (int j : population.get(i)) {
            sum += j * weight.get(cnt++);
        }
        return sum;
    }

    static List<Integer> calFitness(List<List<Integer>> population, int max_weight) {
        List<Integer> fitness = new ArrayList<Integer>();
        int S1, S2;

        for (int i = 0; i < population.size(); i++) {
            S1 = sumValue(population, i);
            S2 = sumWeight(population, i);
            if (S2 <= max_weight)
                fitness.add(S1);
            else
                fitness.add(-S1);
        }
        return fitness;
    }

    static int findMax(List<Integer> fitness) {
        int max = -999999, idx = 0, max_idx = 0;

        for (int i : fitness) {
            if (i > max) {
                max = i;
                max_idx = idx;
            }
            idx++;
        }
        return max_idx;
    }

    static List<List<Integer>> elitism(List<Integer> fitness, List<List<Integer>> population, int num_elites) {
        List<List<Integer>> elites = new ArrayList<List<Integer>>();
        int max_fitness_idx;

        for (int i = 0; i < num_elites; i++) {
            max_fitness_idx = findMax(fitness);
            elites.add(population.get(max_fitness_idx));
        }
        return elites;
    }

    static List<List<Integer>> selection(List<Integer> fitness, List<List<Integer>> population, int num_parents) {
        Random random = new Random();
        List<List<Integer>> parents = new ArrayList<List<Integer>>();
        List<Integer> selected = new ArrayList<Integer>();
        int max_fitness_idx = 0;

        for (int i = 0; i < num_parents; i++) {
            for (int j = 0; j < (int) (0.4 * num_parents); j++) {
                selected.add(fitness.get(random.nextInt(population.size())));
            }
            max_fitness_idx = fitness.indexOf(selected.get(findMax(selected)));
            parents.add(population.get(max_fitness_idx));
            selected.clear();
        }
        return parents;
    }

    static List<List<Integer>> crossover(int crossover_rate, List<List<Integer>> parents, int num_offsprings) {
        Random random = new Random();
        List<List<Integer>> offsprings = new ArrayList<List<Integer>>();
        int[] temp1 = new int[parents.get(0).size() + 1];
        int[] temp2 = new int[parents.get(0).size() + 1];
        int crossover_point1, crossover_point2, i = 0, x = 0;

        crossover_point1 = random.nextInt((int) (parents.get(0).size() / 2) - 1);
        crossover_point2 = random.nextInt((int) (parents.get(0).size() / 2) - 1) + (int) (parents.get(0).size() / 2);

        while (i < num_offsprings - 1) {
            offsprings.add(new ArrayList<Integer>());
            offsprings.add(new ArrayList<Integer>());
            for (int j = 0; j < parents.get(0).size(); j++) {
                temp1[j] = parents.get(i).get(j);
                temp2[j] = parents.get(i + 1).get(j);
                offsprings.get(i).add(temp1[j]);
                offsprings.get(i + 1).add(temp2[j]);
            }

            x = random.nextInt(101);
            if (x > crossover_rate) {
                i += 2;
                continue;
            }
            for (int j = crossover_point1; j < crossover_point2; j++) {
                offsprings.get(i).set(j, parents.get(i + 1).get(j));
                offsprings.get(i + 1).set(j, parents.get(i).get(j));
            }
            i += 2;
        }
        return offsprings;
    }

    static List<List<Integer>> mutation(int mutation_rate, List<List<Integer>> offsprings) {
        Random random = new Random();
        List<List<Integer>> mutants = new ArrayList<List<Integer>>();
        int[] temp = new int[offsprings.get(0).size() + 1];
        int[] num = new int[3];
        int x = 0, random_idx = 0, random_idx1 = 0, random_idx2 = 0;

        for (int i = 0; i < offsprings.size(); i++) {
            mutants.add(new ArrayList<Integer>());
            for (int j = 0; j < offsprings.get(0).size(); j++) {
                temp[j] = offsprings.get(i).get(j);
                mutants.get(i).add(temp[j]);
            }
            x = random.nextInt(101);
            if (x > mutation_rate)
                continue;
            random_idx = random.nextInt(offsprings.get(0).size() - 1);
            if (mutants.get(i).get(random_idx) == 0)
                mutants.get(i).set(random_idx, 1);
            else
                mutants.get(i).set(random_idx, 0);

            for (int j = 0; j < 10; j++) {
                random_idx1 = random.nextInt(offsprings.get(0).size() - 1);
                random_idx2 = random.nextInt(offsprings.get(0).size() - 1);
                num[0] = mutants.get(i).get(random_idx1);
                num[1] = mutants.get(i).get(random_idx2);
                mutants.get(i).set(random_idx1, num[1]);
                mutants.get(i).set(random_idx2, num[0]);
            }
        }
        return mutants;
    }

    static List<List<Integer>> optimize(List<List<Integer>> population, int[] pop_size, int num_gen, int max_weight) {
        List<Integer> fitness = new ArrayList<Integer>();
        List<Integer> selected_items = new ArrayList<Integer>();
        List<List<Integer>> fitness_history = new ArrayList<List<Integer>>();
        List<List<Integer>> elites = new ArrayList<List<Integer>>();
        List<List<Integer>> parents = new ArrayList<List<Integer>>();
        List<List<Integer>> offsprings = new ArrayList<List<Integer>>();
        int cnt = 0, recent_fitness = -999999;

        ////////////////////////////////////////////////////////////////
        int num_elites = (int) (0.04 * pop_size[0]);
        int num_parents = (int) (0.96 * pop_size[0]);
        int num_offsprings = (int) (0.96 * pop_size[0]);
        int crossover_rate = 69, mutation_rate = 49;
        ////////////////////////////////////////////////////////////////

        for (int i = 0; i < num_gen; i++) {
            fitness = calFitness(population, max_weight);
            fitness_history.add(fitness);
            elites = elitism(fitness, population, num_elites);
            parents = selection(fitness, population, num_parents);
            offsprings = crossover(crossover_rate, parents, num_offsprings);
            offsprings = mutation(mutation_rate, offsprings);

            for (int j = 0; j < num_elites; j++) {
                population.set(j, elites.get(j));
            }
            for (int j = num_elites; j < pop_size[0]; j++) {
                population.set(j, offsprings.get(j - num_elites));
            }

            if(fitness.get(findMax(fitness)) == recent_fitness)
                cnt++;
            else{
                recent_fitness = fitness.get(findMax(fitness));
                cnt = 0;
            }

            System.out.println("Gen " + i + ": Best fitness: " + fitness.get(findMax(fitness)));
            if(cnt > 20000)
                break; 
        }
        selected_items = population.get(findMax(fitness));
        System.out.println("\nSelected items that will maximize the knapsack without breaking it:");
        for(int k = 0; k < population.get(0).size(); k++){
            if(selected_items.get(k) == 1)
                System.out.print((k+1) + ", ");
        }   
        System.out.println();

        return fitness_history;
    }

    public static void main(String[] args) throws IOException {
        List<List<Integer>> initial_population = new ArrayList<List<Integer>>();
        List<List<Integer>> fitness_history = new ArrayList<List<Integer>>();
        int solutions_per_pop, num_generations, max_weight;
        int[] pop_size = new int[3];
        int[] n_maxWeight = new int[3];

        ////////////////////////////////////////////////////////////////
        String filename = "./Set2.txt";
        solutions_per_pop = 100;
        num_generations = 100000;
        n_maxWeight = readFile(filename);
        ////////////////////////////////////////////////////////////////

        pop_size[0] = solutions_per_pop;
        pop_size[1] = n_maxWeight[0];
        max_weight = n_maxWeight[1];
        initial_population = initialPopulation(initial_population, solutions_per_pop, value.size());

        long start = System.currentTimeMillis();
        fitness_history = optimize(initial_population, pop_size, num_generations, max_weight);
        float elapsedTimeSec = (System.currentTimeMillis() - start) / 1000F;
        System.out.println("\nRuntime: " + elapsedTimeSec + " seconds");

        FileWriter writer = new FileWriter("output.txt");
        for (List<Integer> x : fitness_history) {
            for (int y : x) {
                writer.write(y + " ");
            }
            writer.write(System.lineSeparator());
        }
        writer.close();
    }
}

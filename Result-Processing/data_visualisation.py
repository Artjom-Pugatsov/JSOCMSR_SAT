import json

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from table_maker import AlgType, StatType, StatisticManager, TableCreator

def create_array(original_array):
    result_array = []

    current_lowest = None

    for i in range(1, 61):
        for tpl in original_array:
            if tpl[1] == i:
                current_lowest = tpl[0]
                break
            elif tpl[1] < i:
                current_lowest = tpl[0]

        result_array.append((current_lowest, i))

    return result_array


def plot_histogram_of_results(data):

    num_jobs = [10,20,50,100,150,200]
    penguin_means = {
        'VSIDS': data[0],
        'Heuristic': data[1],
        'Heuristic + VSIDS': data[2],
    }

    x = np.arange(len(num_jobs))  # the label locations
    width = 0.25  # the width of the bars
    multiplier = 0

    fig, ax = plt.subplots(layout='constrained')

    for attribute, measurement in penguin_means.items():
        offset = width * multiplier
        rects = ax.bar(x + offset, measurement, width, label=attribute)
        ax.bar_label(rects, padding=3)
        multiplier += 1

    # Add some text for labels, title and custom x-axis tick labels, etc.
    ax.set_ylabel('Average makespan % of the highest')
    ax.set_title('Average scaled final lengths of makespans for different heuristics\ngrouped by the number of jobs')
    ax.set_xlabel('Number of jobs')
    ax.set_xticks(x + width, num_jobs)
    ax.legend(loc='upper left', ncols=3)
    ax.set_ylim(60, 120)

    # Display the plot
    plt.show()

def find_max_second_number(file_path):
    max_second_number = None
    #print(file_path)
    with open(file_path, 'r') as file:
        for line in file:
            if line.startswith('m'):
                _, second_number, _ = line.split()
                second_number = int(second_number)

                if max_second_number is None or second_number > max_second_number:
                    max_second_number = second_number
    #print(max_second_number)
    return max_second_number

THRESHOLD_MAPPING = {}

def read__max_duration_best_on_index(prob_type, num_resources, num_jobs, id) -> int:
    path = "ADD_PATH_TO_KEY_FILES"
    filename = prob_type.upper() + "_" + str(num_resources) + "_" + str(num_jobs) + "_" + str(id) + ".key"
    full = path + filename
    #time = find_max_second_number(full)
    time  = THRESHOLD_MAPPING[filename]
    THRESHOLD_MAPPING[filename] = time
    return time

def sortBasedOnVals(vals, freqs):
    zipped = zip(vals, freqs)
    zipped = list(zipped)
    zipped.sort(key=lambda a: a[0])
    vals, freqs = zip(*zipped)
    return (vals, freqs)


class Counter:
    def __init__(self):
        self.vals = {}

    def addValue(self, toAdd):
        if toAdd in self.vals.keys():
            prev = self.vals.get(toAdd)
            self.vals.update({toAdd: prev + 1})
        else:
            self.vals.update({toAdd: 1})

    def getFrequencies(self):
        values = []
        frequencies = []
        for i in self.vals:
            values.append(i)
            frequencies.append(self.vals.get(i))
        return sortBasedOnVals(values, frequencies)


def gen_classifire(prob_type, num_resources, num_jobs):
    return prob_type.lower() + "_" + str(num_resources) + "_" + str(num_jobs)


def degen_classifire(classifire):
    parts = classifire.split("_")
    prob_type = parts[0]
    num_resources = int(parts[1])
    num_jobs = int(parts[2])
    return prob_type, num_resources, num_jobs


class EvaluationInstance:
    def __init__(self, prob_type, num_resources, num_jobs, batch_id, results, status):
        prob_type = prob_type.lower()
        self.prob_type = prob_type
        self.num_resources = num_resources
        self.num_jobs = num_jobs
        self.batch_id = batch_id

        self.results = create_array(results)
        max_threshold = read__max_duration_best_on_index(prob_type, num_resources, num_jobs, batch_id)
        self.max_threshold = max_threshold
        self.results = list(map(lambda x: x if x[0] is not None else (max_threshold, x[1]), self.results))

        self.status = status

    def get_classifire(self):
        return gen_classifire(self.prob_type, self.num_resources, self.num_jobs)



def create_title_average_plot(type, num_resources, num_jobs, heuristic = None):
    type = "skewed" if type.lower() == "s" else "balanced"
    heuristic = "" if heuristic is None else f"\nunder the {heuristic}"
    return f"Average makespan for {type} problem instances\nwith {num_resources} secondary resources and {num_jobs} jobs{heuristic}"


def gen_values_for_averages(data):
    x_values = range(1, 61)
    y_values = []

    for i in x_values:
        values = []
        for result in data:
            if result[i - 1][0] is not None:
                values.append(result[i - 1][0])
        # print(values)
        average = sum(values) / len(values) if values else 0
        y_values.append(average)
    return (x_values, y_values)


def create_average_plot(data, instance_name):
    (x_values, y_values) = gen_values_for_averages(data)

    plt.plot(x_values, y_values)
    plt.ylim([min(y_values) - min(y_values)*0.1, max(y_values) + max(y_values)*0.1])
    plt.xlabel('placeholder')
    plt.ylabel('placeholder')
    clasif = degen_classifire(instance_name)
    plt.title(f"{create_title_average_plot(clasif[0], clasif[1], clasif[2])}")
    plt.show()

def create_average_plot_multiple(datas):
    values = []
    for i in datas:
        values.append(gen_values_for_averages(i))
    for i in values:
        plt.plot(i[0], i[1])
    plt.xlabel('Time in seconds from start')
    plt.ylabel('Average makespan')
    plt.title(f"Average best-so-far makespan for problem instances\nwith 150 jobs for variable selection + VSIDS")
    plt.legend( ["Balanced with 3 resources", "Balanced with 5 resources", "Skewed with 3 resources", "Skewed with 5 resources"], loc=0)
    plt.show()



class DataManager:

    def __init__(self, df: pd.DataFrame, heuristic_type: AlgType = None):
        self.df = df
        self.mapping = {}
        self.allEvalInstances = []
        self.classifires = []
        self.heuristic_type = heuristic_type
        for column in df:
            v = df[column].values
            ev_inst = EvaluationInstance(v[0], v[4], v[3], v[6], v[2], v[7])
            self.allEvalInstances.append(ev_inst)
            if not ev_inst.get_classifire() in self.mapping:
                self.classifires.append(ev_inst.get_classifire())
                self.mapping[ev_inst.get_classifire()] = []
            self.mapping[ev_inst.get_classifire()].append(ev_inst)
        self.classifires.sort(key=lambda x: degen_classifire(x))

    def index_data(self, prob_type, num_resources, num_jobs) -> [EvaluationInstance]:
        prob_type = prob_type.lower()
        return self.mapping[gen_classifire(prob_type, num_resources, num_jobs)]

    def add_results(self, statManager: StatisticManager):
        for i_1 in ["b", "s"]:
            for i_2 in [3,5]:
                for i_3 in [10,20,50,100,150,200]:
                    instance = self.index_data(i_1, i_2, i_3)
                    info_all = []
                    for i in instance:
                        results = i.results
                        final_score = results[len(results)-1][0]
                        last_imp = None

                        for j in range(len(results) - 1, 0, -1):
                            current_first = results[j][0]
                            previous_first = results[j - 1][0]
                            if current_first != previous_first:
                                last_imp = results[j][1]
                                break

                        index = 60
                        if last_imp is None:
                            last_imp = 1
                        for j, (first, sec) in enumerate(results):
                            if first < i.max_threshold:
                                index = sec
                                break
                        first_improvement_time = index
                        status = i.status
                        info_all.append((final_score, first_improvement_time, last_imp, status))
                    m = sum(t[0] for t in info_all) / len(info_all)
                    first_imp = sum(t[1] for t in info_all) / len(info_all)
                    last_imp = sum(t[2] for t in info_all) / len(info_all)
                    individual_times = list(map(lambda x: x[0], info_all))

                    elements = list(map(lambda t: t[3], info_all))
                    count_a = elements.count("OPTIMAL")
                    count_b = elements.count("SATISFIABLE")
                    count_c = elements.count("UNKNOWN")
                    statuses = (count_a, count_b, count_c)
                    statManager.add_data(i_1, i_2, i_3, self.heuristic_type, StatType.M, m)
                    statManager.add_data(i_1, i_2, i_3, self.heuristic_type, StatType.INDIVIDUAL_TIMES, individual_times)
                    statManager.add_data(i_1, i_2, i_3, self.heuristic_type, StatType.STATUSES, statuses)
                    statManager.add_data(i_1, i_2, i_3, self.heuristic_type, StatType.T_BEST, last_imp)
                    statManager.add_data(i_1, i_2, i_3, self.heuristic_type, StatType.T_FIRST, first_imp)




if __name__ == '__main__':

    # Read thresholds if they have been generated before
    # with open("thresholds.json", "r") as file:
    # THRESHOLD_MAPPING = json.load(file)

    # Read data
    df_baseline = pd.read_json('ADD_PATH')
    df_no_visids = pd.read_json("ADD_PATH")
    df_vsids_heuristic = pd.read_json('ADD_PATH')
    dm_baseline = DataManager(df_baseline, AlgType.VSIDS)
    dm_heuristic_no_visids = DataManager(df_no_visids, AlgType.HEURISTIC)
    dm_vsids_heuristic = DataManager(df_vsids_heuristic, AlgType.VSIDS_HEURISTIC)
    stat_manager = StatisticManager()
    dm_baseline.add_results(stat_manager)
    dm_heuristic_no_visids.add_results(stat_manager)
    dm_vsids_heuristic.add_results(stat_manager)

    # Do processing
    baseline_avg = []
    heurist_avg = []
    heurist_VSIDS_avg = []
    for num_jobs in [10,20,50,100,150,200]:
        baseline_avg.append(stat_manager.get_average(AlgType.VSIDS, StatType.M, prob_type=None, num_jobs=num_jobs))
        heurist_avg.append(stat_manager.get_average(AlgType.HEURISTIC, StatType.M, prob_type=None, num_jobs=num_jobs))
        heurist_VSIDS_avg.append(stat_manager.get_average(AlgType.VSIDS_HEURISTIC, StatType.M, prob_type=None, num_jobs=num_jobs))
    max_values = np.maximum.reduce([baseline_avg, heurist_avg, heurist_VSIDS_avg])

    # Scale the arrays proportionally
    scaled_arr1 = (baseline_avg / max_values) * 100
    scaled_arr1 = list(map(lambda x: round(x, 0), scaled_arr1))
    scaled_arr2 = (heurist_avg / max_values) * 100
    scaled_arr2 = list(map(lambda x: round(x, 0), scaled_arr2))
    scaled_arr3 = (heurist_VSIDS_avg / max_values) * 100
    scaled_arr3 = list(map(lambda x: round(x, 0), scaled_arr3))


    # Combine the scaled arrays into a 2D array
    result = np.vstack((scaled_arr1, scaled_arr2, scaled_arr3))
    print(result)
    print(result[0])

    plot_histogram_of_results(result)

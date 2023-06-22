# m - average final makespan. If no makespan was found
# it is set to Tmax.
# • tf irst - average time in seconds to an initial solution. If
# no solution was found it is set to 60 seconds.
# • tbest - average time at which the best solution was found.
# Also is set to 60 if no solution was found.
# • %best - the percentage of instances that are equal to the
# best value found by any heuristic.
# • s - statuses of jobs in the batch. It is in the form x/y/z, where x-Optimal, y-satisfiable, z-unknown

from enum import Enum


# class syntax
class AlgType(Enum):
    __order__ = " VSIDS VSIDS_HEURISTIC HEURISTIC "
    VSIDS = 1
    VSIDS_HEURISTIC = 2
    HEURISTIC = 3


class StatType(Enum):
    M = 1
    T_FIRST = 2
    T_BEST = 3
    PERCENT_BEST = 4
    STATUSES = 5
    INDIVIDUAL_TIMES = 6


# functional syntax
Color = Enum('Color', ['RED', 'GREEN', 'BLUE'])


def _gen_index(prob_type: str, num_resources: int, num_jobs: int, heuristic: AlgType, stat_type: StatType):
    toRet = f"{prob_type.lower()}_{num_resources}_{num_jobs}_{heuristic}_{stat_type}"
    #print(stat_type)
    #print(toRet)
    return toRet


class StatisticManager:
    records = {}

    def __init__(self):
        self.records = {}

    def add_data(self, prob_type: str, num_resources: int, num_jobs: int, heuristic: AlgType, stat_type: StatType, stat_value):
        prob_type = prob_type.lower()
        self.records[_gen_index(prob_type, num_resources, num_jobs, heuristic, stat_type)] = stat_value

    def get_data(self, prob_type: str, num_resources: int, num_jobs: int, heuristic: AlgType, stat_type: StatType):
        prob_type = prob_type.lower()
        g = _gen_index(prob_type, num_resources, num_jobs, heuristic, stat_type)
        if _gen_index(prob_type, num_resources, num_jobs, heuristic, stat_type) in self.records or stat_type== StatType.PERCENT_BEST:
            if stat_type == StatType.PERCENT_BEST:
                # Get best value
                gather_all = []
                for alg_types in AlgType:
                    res = (self.get_data(prob_type, num_resources, num_jobs, alg_types, StatType.INDIVIDUAL_TIMES))
                    if res is not None:
                        gather_all.append(res)
                result = [min(items) for items in zip(*gather_all)]
                self_data = self.get_data(prob_type, num_resources, num_jobs, heuristic, StatType.INDIVIDUAL_TIMES)
                return sum(x == y for x, y in zip(result, self_data))/len(self_data) *100

            else:
                return self.records[_gen_index(prob_type, num_resources, num_jobs, heuristic, stat_type)]
        else:
            return None

    def get_average(self, heuristic: AlgType, statistic_type: StatType,  prob_type = None, num_resources = None, num_jobs = None ):
        results = []
        if prob_type is None:
            prob_type = ["b", "s"]
        elif type(prob_type) != list:
            prob_type = [prob_type]
        if num_jobs is None:
            num_jobs = [10,20, 50, 100, 150, 200]
        elif type(num_jobs) != list:
            num_jobs = [num_jobs]
        if num_resources is None:
            num_resources = [3, 5]
        elif type(num_resources) != list:
            num_resources = [num_resources]
        for p_t in prob_type:
            for n_r in num_resources:
                for n_j in num_jobs:
                    results.append(self.get_data(p_t, n_r, n_j, heuristic, statistic_type))
        return sum(results)/len(results)

def format_statuses(statuses):
    return f"{statuses[0]}/{statuses[1]}/{statuses[2]}"

class TableCreator:
    def __init__(self, statManager: StatisticManager):
        self.statManager = statManager


    def gen_line(self, prob_type: str, num_jobs: int, num_resources: int):
        prob_marker = "s" if prob_type.lower() == "s" else "b"
        first = f"{prob_marker} & {num_jobs} & {num_resources}"
        basic = self.gen_stat__for_heuristic_type(prob_type, num_jobs, num_resources, AlgType.VSIDS)
        heuristic = self.gen_stat__for_heuristic_type(prob_type, num_jobs, num_resources, AlgType.HEURISTIC)
        heurist_visids = self.gen_stat__for_heuristic_type(prob_type, num_jobs, num_resources, AlgType.VSIDS_HEURISTIC)
        return f"{first}{basic}{heuristic}{heurist_visids}\\\\\n"

    def gen_chunk(self, prob_type: str, num_resources: int):
        i_10 = self.gen_line(prob_type,10, num_resources)
        i_20 = self.gen_line(prob_type,20, num_resources)
        i_50 = self.gen_line(prob_type,50, num_resources)
        i_100 = self.gen_line(prob_type,100, num_resources)
        i_150 = self.gen_line(prob_type,150, num_resources)
        i_200 =self.gen_line(prob_type,200, num_resources)
        return f"{i_10}{i_20}{i_50}{i_100}{i_150}{i_200}"

    def gen_all(self):
        b_3 = self.gen_chunk("b", 3)
        b_5 = self.gen_chunk("b", 5)
        s_3 = self.gen_chunk("s", 3)
        s_5 = self.gen_chunk("s", 5)
        return f"{b_3}\\hline\\hline\n{b_5}\\hline\\hline\n{s_3}\\hline\\hline\n{s_5}\\hline"


    def gen_stat__for_heuristic_type(self, prob_type: str, num_jobs: int, num_resources: int, heuristic: AlgType):
        m = self.statManager.get_data(prob_type, num_resources, num_jobs, heuristic, StatType.M)
        p_b = self.statManager.get_data(prob_type, num_resources, num_jobs, heuristic, StatType.PERCENT_BEST)
        s = self.statManager.get_data(prob_type, num_resources, num_jobs, heuristic, StatType.STATUSES)
        t_b = self.statManager.get_data(prob_type, num_resources, num_jobs, heuristic, StatType.T_BEST)
        t_f = self.statManager.get_data(prob_type, num_resources, num_jobs, heuristic, StatType.T_FIRST)
        s = format_statuses(s)
        return f" & {round(m,1)} & {round(p_b)} & {s} &{round(t_f,1)} & {round(t_b,1)}"

    def header(self):
        return ""




if __name__ == '__main__':
    g = StatisticManager()
    g.add_data("s", 3, 10, AlgType.VSIDS, StatType.INDIVIDUAL_TIMES, [1,2,3,0])
    g.add_data("s", 3, 10, AlgType.VSIDS_HEURISTIC, StatType.INDIVIDUAL_TIMES, [1, 5, 3,1])
    print(g.get_data("s", 3, 10, AlgType.VSIDS, StatType.PERCENT_BEST))

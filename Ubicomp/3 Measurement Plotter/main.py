import csv
import re
import matplotlib.pyplot as plt

all = ["UNKNOWN", "IN_VEHICLE", "ON_BICYCLE", "ON_FOOT", "WALKING", "RUNNING", "STILL"]

def load_data(path):
    data = []
    with open(path, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=",")
        for row in reader:
            time = row[0]
            activities = []
            for activity in row[1:len(row)]:
                split = activity.split(':')
                type = split[0]
                confidence = int(split[1])
                activities.append([type, confidence])
            data.append([time, activities])
    return data


def in_tuple_list(value, list):
    for tuple in list:
        for v in tuple:
            if v == value:
                return True
    return False


def tuple_list_to_dict(list):
    dict = {}
    for tuple in list:
        dict[tuple[0]] = tuple[1]
    return dict


data = load_data("data.csv")
pattern = re.compile("\d\d:\d\d:\d\d")

plots = {}
for date in data:
    for activity in all:
        x = re.search(pattern, date[0]).group(0)
        activity_dict = tuple_list_to_dict(date[1])

        if activity in activity_dict:
            y = activity_dict[activity]
        else:
            y = 0

        if activity not in plots:
            plots[activity] = []
        plots[activity].append([x, y])

keys = plots.keys()

for key in keys:
    x = []
    y = []
    for values in plots[key]:
        x.append(values[0])
        y.append(values[1])

    plt.plot(x, y, label=key)

plt.legend()
plt.xticks(rotation=30)
plt.show()

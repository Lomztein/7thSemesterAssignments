import csv
import re
import matplotlib.pyplot as plt


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


data = load_data("data.csv")

plots = {}
for date in data:
    for activity in date[1]:

        pattern = re.compile("\d\d:\d\d:\d\d")
        x = re.search(pattern, date[0]).group(0)
        y = activity[1]

        if activity[0] not in plots:
            plots[activity[0]] = []
        plots[activity[0]].append([x, y])

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
print(plots)

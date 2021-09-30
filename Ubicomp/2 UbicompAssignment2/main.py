import csv
import folium
import pandas as pd
import webbrowser
import math

timestamp_ms = 'timestamp_ms'
gt_lat = 'gt_lat'
gt_long = 'gt_long'
phone_lat = 'phone_lat'
phone_long = 'phone_long'
n = 10

def load_data(path):
    data = []
    with open(path, newline='') as csvfile:
        next(csvfile)
        reader = csv.reader(csvfile, delimiter=',')
        for row in reader:
            data.append(
                {
                    timestamp_ms: int(row[0]),
                    gt_lat: float(row[1]),
                    gt_long: float(row[2]),
                    phone_lat: float(row[3]),
                    phone_long: float(row[4])
                })
    return data


# There's gotta be a better way of doing this..
def compute_rect(data):
    lat_min = float('inf')
    lat_max = -float('inf')
    long_min = float('inf')
    long_max = -float('inf')

    for row in data:
        lat_min = min(lat_min, row['gt_lat'], row['phone_lat'])
        lat_max = max(lat_max, row['gt_lat'], row['phone_lat'])

        long_min = min(long_min, row['gt_long'], row['phone_long'])
        long_max = max(long_max, row['gt_long'], row['phone_long'])

    return {
        'lat_min': lat_min,
        'lat_max': lat_max,
        'long_min': long_min,
        'long_max': long_max,
    }


def rect_center(rect):
    return {
        'lat': rect['lat_min'] + (rect['lat_max'] - rect['lat_min']) / 2,
        'long': rect['long_min'] + (rect['long_max'] - rect['long_min']) / 2
    }

dedicated_lat_index = 1
dedicated_long_index = 2
phone_lat_index = 3
phone_long_index = 4

def lat_index(dedicated):
    if dedicated:
        return dedicated_lat_index
    else:
        return phone_lat_index


def long_index(dedicated):
    if dedicated:
        return dedicated_long_index
    else:
        return phone_long_index


def no_filter(numbers, index, n):
    return numbers[index]


def data_to_raw_path(data, dedicated):
    return generic_filter(data, dedicated, no_filter)


def mean(numbers, index, n):
    start = max(index - n, 0)
    sum = 0
    for i in range(start, index + 1):
        sum += numbers[i]
    amount = index - start + 1
    result = sum / amount
    return result


def mean_filter_to_path(data, dedicated):
    return generic_filter(data, dedicated, mean)


def median(numbers, index, n):
    start = max(index - n, 0)
    range = list(numbers[start:index+1])
    range.sort()

    size = index - start
    middle = size / 2

    rh = range[int(math.floor(middle))]
    lh = range[int(math.ceil(middle))]
    result = (rh + lh) / 2

    return result


def median_filter_to_path(data, dedicated):
   return generic_filter(data, dedicated, median)


def generic_filter(data, dedicated, filter_func):
    output = []
    lat = lat_index(dedicated)
    long = long_index(dedicated)

    lats = all_at_dict_index(data, lat)
    longs = all_at_dict_index(data, long)

    for i in range(0, len(data)):
        local_lat = filter_func(lats, i, n)
        local_long = filter_func(longs, i, n)

        output.append([local_lat, local_long])
    return output


def all_at_dict_index(dict, dim):
    result = []
    for row in dict:
        result.append(list(row.values())[dim]) # idk
    return result


def draw_path(map, path, color, weight, opacity = 0.8):
    last_lat = path[0][0]
    last_long = path[0][1]
    for i in range (1, len(path)):
        loc = [(last_lat, last_long), (path[i][0], path[i][1])]
        folium.PolyLine(loc, color=color, weight=weight, opacity=opacity).add_to(map)

        last_lat = path[i][0]
        last_long = path[i][1]


if __name__ == "__main__":
    data = load_data('data/Biking.csv')
    rect = compute_rect(data);

    center = rect_center(rect)

    path_map = folium.Map(location = [center['lat'], center['long']], zoom_start=15)

    raw_path = data_to_raw_path(data, True)
    mean_path = mean_filter_to_path(data, True)
    median_path = median_filter_to_path(data, True);

    draw_path(path_map, raw_path, 'gray', 3)
    draw_path(path_map, mean_path, 'red', 3)
    draw_path(path_map, median_path, 'black', 3)

    path_map.save('map.html')
    webbrowser.open('map.html')


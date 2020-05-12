import pandas as pd
import sys

print(sys.argv[2])
data = pd.read_table('/data/'+sys.argv[2])
data.to_csv('/tmp/data.csv', index = False)
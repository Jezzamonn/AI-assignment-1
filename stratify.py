import pandas as pd

headers = ['preg','glucos','bloodpresh','skinthickness','serum','bmi','dpf','age','class']
df = pd.read_csv("pima.csv", header=None, names=headers, dtype='str')
df_yes = df[df['class'] == 'yes']
df_no = df[df['class'] == 'no']
n_folds = 10

with open('pima-folds.csv', 'w') as f:
    for i in range(n_folds):
        f.write('fold{}\n'.format(i+1))
        fold_yes = df_yes[i::n_folds]
        fold_no = df_no[i::n_folds]
        f.write(fold_yes.to_csv(header=False, index=False))
        f.write(fold_no.to_csv(header=False, index=False))
        f.write('\n')
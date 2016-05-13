mat = [
    ['m00', 'm01', 'm02', 'm03'],
    ['m10', 'm11', 'm12', 'm13'],
    ['m20', 'm21', 'm22', 'm23'],
    ['m30', 'm31', 'm32', 'm33'],
]

line = "double {0} = " \
"{1} * {2} * {3} + " \
"{4} * {5} * {6} + " \
"{7} * {8} * {9} - " \
"{10} * {11} * {12} - " \
"{13} * {14} * {15} - " \
"{16} * {17} * {18};"

cofactor_idxs = [0, 4, 8, 1, 5, 6, 2, 3, 7, 6, 4, 2, 7, 5, 0, 3, 1, 8]

print('/* Generated with autojava.py */')
for row in range(4):
    for col in range(4):
        cofactor_vals = []
        cofactor_name = 'c' + mat[row][col][1:]

        for i in range(4):
            for j in range(4):
                if i != row and j != col:
                    cofactor_vals.append(mat[i][j])

        params = []
        for idx in cofactor_idxs:
            params.append(cofactor_vals[idx])

        print(line.format(cofactor_name, *params))

print('/* Generated with autojava.py - end */')

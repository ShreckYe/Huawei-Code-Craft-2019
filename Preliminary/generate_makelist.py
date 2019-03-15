import os
import io


def add_all_files(file_list: list, root: str, relative_path: str = ""):
    files = os.listdir(os.path.join(root, relative_path))
    for filename in files:
        file_relative_path = os.path.join(relative_path, filename)
        if os.path.isdir(os.path.join(root, file_relative_path)):
            add_all_files(file_list, root, file_relative_path)
        else:
            file_list.append(file_relative_path)


source_file_list = []
add_all_files(source_file_list, "code/CodeCraft-2019/src/main/java")
source_file_list = list(map(lambda path: os.path.join("main/java", path).replace("\\", "/"), source_file_list))
print(source_file_list)

with io.open("makelist.txt", mode="w", newline="\n") as file:
    for source_file in source_file_list:
        file.write(source_file)
        file.write("\n")

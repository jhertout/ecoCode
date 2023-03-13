import copy

def copy_method():
    x = []
    return copy.copy(x)# Noncompliant {{Avoid using Lib/copy.copy(x)}}

import copy

def deepcopy_method():
    return copy.deepcopy([])# Noncompliant {{Avoid using Lib/copy.deepcopy(x)}}

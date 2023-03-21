import copy
import array

class TestObject():

    def __init__(self, prop1, prop2):
        self.prop1 = prop1
        self.prop2 = prop2

    def __str__(self):
        return self.prop1 + " " +  self.prop2

    def listFromObject(self) -> list:
        return [self.prop1, self.prop2]

    def untypedlistFromObject(self) :
        return [self.prop1, self.prop2]

    def append(list):
        return list

# List copy

list1 = [1, 2, [3, 4], 5]

new_list = []
for item in [0,1,2,3,4,5,6,7,8,9]:
    new_list.append(item.tolist())# Noncompliant {{Using `list.append(x)` within a loop to copy a list is not energy efficient.}}

testObj = TestObject()
for item in [0,1,2,3,4,5,6,7,8,9]:
    testObj.append(list1)# NO ISSUE

new_list = []
while True:
    new_list.append([0,1,2,3,4,5,6,7,8,9])# Noncompliant {{Using `list.append(x)` within a loop to copy a list is not energy efficient.}}




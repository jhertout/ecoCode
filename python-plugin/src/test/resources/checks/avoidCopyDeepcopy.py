import copy
import array

class TestObject():

    def __init__(self, prop1, prop2):
        self.prop1 = prop1
        self.prop2 = prop2

    def __str__(self):
        return self.prop1 + " " +  self.prop2

    def listFromObject(self):
        return [self.prop1, self.prop2]

    def deepcopy(self, list):
        return list


# List deepcopy

list1 = [1, 2, [3, 4], 5]
list_copy1 = copy.deepcopy(list1) # Noncompliant {{Avoid using Lib/deepcopy.copy(x)}}

list2 = [TestObject(1, "test1"), TestObject(2, "test2")]
list_copy2 = copy.deepcopy(list2) # Noncompliant {{Avoid using Lib/deepcopy.copy(x)}}

list3 = TestObject(1, "test1").listFromObject() # Noncompliant {{Avoid using Lib/deepcopy.copy(x)}}
list_copy3 = copy.deepcopy(list3)

# Array deepcopy

array1 = array.array('i', [1, 2, 3])
array_copy1 = copy.deepcopy(array1) # NO issue
array_copy2 = copy.deepcopy(array.array('i', [1, 2, 3])) # NO issue

# Object deepcopy

object1 = "test"
object_copy11 = copy.deepcopy(object1) # NO issue
object_copy12 = copy.deepcopy("test") # NO issue

object2 = TestObject("test 1", "test 2")
object_copy21 = copy.deepcopy(object2) # NO issue
object_copy22 = copy.deepcopy(TestObject("test 1", "test 2")) # NO issue

# Other deepcopy()

object3 = TestObject("test 1", "test 2")
other_list = object3.deepcopy([1, 2]) # NO issue

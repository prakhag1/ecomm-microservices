import json

# Loop through products json to find matching products by category
def findMatchingProducts(categories):
	list = []
	with open('products.json') as products:
	   data = json.load(products)
	   for key, value in enumerate(d.items() for d in data):
	      match = any(item in data[key]['categories'] for item in categories)
	      if match is True:
	         list.append(data[key]['id'])
	  
	return list   
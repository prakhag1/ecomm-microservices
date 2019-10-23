import grpc
from concurrent import futures
import time

# import the generated classes
import demo_pb2
import demo_pb2_grpc
import recommend

#import google.cloud.logging
#import logging
#client = google.cloud.logging.Client()
#client.setup_logging()

# create a class to define the server functions, derived from demo_pb2_grpc.RecommendationServiceServicer
class RecommendationServiceServicer(demo_pb2_grpc.RecommendationServiceServicer):
	
    def ListRecommendations(self, request, context):    	
        response = demo_pb2.ListRecommendationsResponse()
        response.product_ids[:] = recommend.findMatchingProducts(request.product_category)
        #logging.info('Returning recommendations...')
        return response


# create a gRPC server
server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))

# use the generated function `add_RecommendationServiceServicer_to_server` to add the defined class to the server
demo_pb2_grpc.add_RecommendationServiceServicer_to_server(RecommendationServiceServicer(), server)

# listen on port 50051
#logging.info('Starting server. Listening on port 50051.')
print('Starting server')
server.add_insecure_port('[::]:50051')
server.start()

# since server.start() will not block,
# a sleep-loop is added to keep alive
try:
    while True:
        time.sleep(86400)
except KeyboardInterrupt:
    server.stop(0)

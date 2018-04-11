"""Python test fixture to check that watson sdk loads"""

from watson_developer_cloud import DiscoveryV1

def main(dict):
    return {"message": DiscoveryV1.default_url}

if __name__ == "__main__":
    # execute only if run as a script
    print(main({}))

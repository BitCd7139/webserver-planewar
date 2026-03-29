#include<iostream>
#include"pool/threadpool.h"
#include"log/logger.h"
#include"server/epoller.h"
#include "server/server.h"
using namespace std;

void test_func(int a, int b) {
    printf("Task running, id: %d\n", a);
    LOG_DEBUG("Task running, id: %d", a);
}

#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <cstring>
#include <vector>
#include "server/epoller.h"

using namespace webserver;

int main() {
    webserver::Server server(12345, Log::LogLevel::DEBUG);
    server.run();

    return 0;
}
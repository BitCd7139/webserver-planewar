#include <stdio.h>
#include <sys/select.h>
#include <string.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/select.h>
#include <unistd.h>

int main() {
    char* ipaddr = "0.0.0.0";
    int port = 12345;
    int sock_fd = socket(AF_INET, SOCK_STREAM, 0);

    int opt = 1;
    setsockopt(sock_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    struct sockaddr_in serv_addr;
    memset(&serv_addr, 0, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = inet_addr(ipaddr);
    serv_addr.sin_port = htons(port);

    bind(sock_fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
    listen(sock_fd, 10);
    printf("Server listening on port %d...\n", port);

    fd_set rfds, rset;
    FD_ZERO(&rfds);
    FD_SET(sock_fd, &rfds);
    int max_fd = sock_fd;

    while(1) {
        rset = rfds;

        int nready = select(max_fd+1, &rset, NULL, NULL, NULL);
        if (nready < 0) {
            perror("select error");
            break;
        }

        if(FD_ISSET(sock_fd, &rset)) {
            struct sockaddr_in clnt_addr;
            socklen_t clnt_addr_len = sizeof(clnt_addr);

            int clnt_fd = accept(sock_fd, (struct sockaddr*)&clnt_addr, &clnt_addr_len);
            printf("sockfd: %d\n", sock_fd);

            FD_SET(clnt_fd, &rfds);
            max_fd = clnt_fd;
        }

        for(int i = sock_fd+1; i <= max_fd; i ++) {
            char buffer[1024] = {0};
            if(FD_ISSET(i, &rset)) {
                int count = recv(i, buffer, sizeof(buffer), 0);
                if(count <= 0) {
                    printf("Client disconnected: %d\n", i);

                    FD_CLR(i, &rfds);
                    close(i);
                }
                else {
                    printf("Received from client %d: %s\n", i, buffer);
                    send(i, buffer, count, 0);

                }
            }
        }
    }
    close(sock_fd);
}

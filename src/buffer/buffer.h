#ifndef WEBSERVER_BUFFER_H
#define WEBSERVER_BUFFER_H

#include <vector>
#include <string>
#include <string_view>
#include <cstddef>
#include <algorithm>

namespace webserver {

    class Buffer {
    public:
        explicit Buffer(int initBuffSize = 1024) : buffer_(initBuffSize) {}
        ~Buffer() = default;

        [[nodiscard]] std::size_t writable_bytes() const noexcept;
        [[nodiscard]] std::size_t readable_bytes() const noexcept;
        [[nodiscard]] std::size_t prependable_bytes() const noexcept;

        [[nodiscard]] const char* peek() const noexcept;

        void EnsureWriteable(std::size_t len);
        void AfterWrite(std::size_t len) noexcept;

        void Retrieve(std::size_t len) noexcept;
        void RetrieveUntil(const char* end) noexcept;

        void RetrieveAll() noexcept;

        [[nodiscard]] std::string RetrieveAllToStr();

        [[nodiscard]] const char* BeginWriteConst() const noexcept;
        [[nodiscard]] char* BeginWrite() noexcept;

        void Append(std::string_view str);
        void Append(const void* data, std::size_t len);
        void Append(const char *str, size_t len);
        void Append(const Buffer& buff);

        [[nodiscard]] ssize_t ReadFd(int fd, int* Errno);
        [[nodiscard]] ssize_t WriteFd(int fd, int* Errno);

        //TODO:test
        const char* find_crlf() const {
            // HTTP 头部结束标志
            const char* target = "\r\n\r\n";

            // 使用 std::search 在 [peek(), peek() + size] 范围内查找 target
            auto it = std::search(peek(), peek() + readable_bytes(),
                                 target, target + 4);

            // 如果没找到，std::search 会返回 end 指针（即 peek() + readable_bytes()）
            if (it == peek() + readable_bytes()) {
                return nullptr;
            }
            return it;
        }


    private:
        char* BeginPtr_() noexcept;
        [[nodiscard]] const char* BeginPtr_() const noexcept;
        void MakeSpace_(std::size_t len);

        std::vector<char> buffer_;
        std::size_t read_pos_{0};
        std::size_t write_pos_{0};
    };

} //  webserver

#endif // WEBSERVER_BUFFER_H
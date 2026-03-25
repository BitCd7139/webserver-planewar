#ifndef WEBSERVER_LOGGER_H
#define WEBSERVER_LOGGER_H
#include <mutex>
#include <thread>
#include <chrono>
#include <filesystem>
#include <fstream>
#include <sstream>
#include <string_view>
#include "base/lock_free_queue.hpp"

namespace webserver {
    namespace fs = std::filesystem;

    class Log {
    public:
        static Log& instance() {
            static Log instance;
            return instance;
        }

        enum class LogLevel : int {
            DEBUG = 0,
            INFO = 1,
            WARN = 2,
            ERROR = 3,
            FATAL = 4
        };

        Log(const Log&) = delete;
        Log& operator=(const Log&) = delete;

        void init(LogLevel level, const fs::path& path = "./log",
              std::string_view suffix = ".log",
              int maxQueueSize = 1024);

        template<typename... Args>
        void write(int level, std::string_view message, Args&&... args);

        void flush();

    private:
        struct LogEvent {
            LogLevel level;
            fs::path filename;
            std::string message;
            std::chrono::time_point<std::chrono::system_clock> time;
            uint line;
        };

        Log() = default;
        ~Log();

        void async_write();

        fs::path log_path_;
        LogLevel log_level_ = LogLevel::DEBUG;
        bool is_async_ = false;
        bool is_open_ = false;

        std::ofstream log_file_;
        std::unique_ptr<webserver::LockFreeQueue<std::string>> log_queue_;
        std::unique_ptr<std::thread> thread_;
        std::mutex mutex_;
    };

    #define LOG_BASE(level, format, ...) \
    Log::Instance().write(level, __FILE__, __LINE__, format, ##__VA_ARGS__)

    #define LOG_DEBUG(format, ...) LOG_BASE(0, format, ##__VA_ARGS__)
    #define LOG_INFO(format, ...)  LOG_BASE(1, format, ##__VA_ARGS__)
    #define LOG_WARN(format, ...)  LOG_BASE(2, format, ##__VA_ARGS__)
    #define LOG_ERROR(format, ...) LOG_BASE(3, format, ##__VA_ARGS__)
    #define LOG_FATAL(format, ...) LOG_BASE(4, format, ##__VA_ARGS__)
}

#endif //WEBSERVER_LOGGER_H
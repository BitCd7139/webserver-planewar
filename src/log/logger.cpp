#include "logger.h"

#include <queue>

namespace webserver {
    Log::init(LogLevel level = LogLevel::DEBUG, const fs::path& path = "./log",
              std::string_view suffix = ".log",
              int maxQueueSize = 1024) {
        log_path_ = path;
        log_level_ = level;
        log_queue_ = std::make_unique<LockFreeQueue<std::string>>(maxQueueSize);
        fs::create_directories(path);
    }

}

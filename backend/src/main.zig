const std = @import("std");

const CompletionRequest = struct {
    request_id: u32,
    type: []const u8,
    text: []const u8,
    cursor: usize,
};

const CompletionResponse = struct {
    request_id: u32,
    type: []const u8,
    items: [][]const u8,
};

pub fn main() !void {
    var da = std.heap.DebugAllocator(.{}).init;
    defer {
        const deinit = da.deinit();
        std.debug.assert(deinit != std.heap.Check.leak);
    }
    const allocator = da.allocator();

    const input_buffer = try allocator.alloc(u8, 1024);
    defer allocator.free(input_buffer);
    var stdin = std.fs.File.stdin().reader(input_buffer);

    const output_buffer = try allocator.alloc(u8, 1024);
    defer allocator.free(output_buffer);
    var stdout = std.fs.File.stdout().writer(output_buffer);

    while (true) {
        const line = try stdin.interface.takeDelimiter('\n') orelse break;

        var parsed: std.json.Parsed(CompletionRequest) = try std.json.parseFromSlice(
            CompletionRequest,
            allocator,
            line,
            .{},
        );
        defer parsed.deinit();
        const request = parsed.value;

        if (std.mem.eql(u8, request.type, "completionContext")) {
            if (request.cursor > 0 and request.text[request.cursor - 1] == '.') {
                var items = [_][]const u8{"id","name","email"};
                const response = CompletionResponse{
                    .request_id = request.request_id,
                    .type = "completionResult",
                    .items = &items,
                };
                try stdout.interface.print(
                    "{f}\n",
                    .{
                        std.json.fmt(
                            response,
                            .{
                                .whitespace = .minified
                            },
                        ),
                    },
                );
            } else {
                const response = CompletionResponse{
                    .request_id = request.request_id,
                    .type = "completionResult",
                    .items = &[_][]const u8{},
                };
                try stdout.interface.print(
                    "{f}\n",
                    .{
                        std.json.fmt(
                            response,
                            .{
                                .whitespace = .minified
                            },
                        ),
                    },
                );
            }
        }
        try stdout.interface.flush();
    }
}

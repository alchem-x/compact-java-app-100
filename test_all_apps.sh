#!/bin/bash

# 精简版 - 一键测试所有Java应用
# 测试所有101个Java应用，每个运行3秒后自动关闭

echo "🚀 测试所有Java应用 (101个)"
echo "=========================="

# 获取所有应用列表
apps=$(ls apps/*.java | sed 's|apps/||g' | sed 's|\.java||g' | sort)
total=$(echo "$apps" | wc -l)
echo "找到 $total 个应用"
echo ""

# 初始化计数器
success=0
failed=0
failed_apps=""

# 开始测试
echo "开始测试 (每1秒启动一个应用，每个运行3秒后自动关闭)..."
echo ""

app_num=0
for app in $apps; do
    app_num=$((app_num + 1))
    echo "[$app_num/$total] 测试: $app"

    # 启动应用并立即设置超时
    {
        java "apps/$app.java" > /dev/null 2>&1 &
        pid=$!

        # 等待3秒
        sleep 3

        # 如果进程还在运行，则kill掉
        if kill -0 $pid 2> /dev/null; then
            kill -9 $pid 2> /dev/null
            wait $pid 2> /dev/null
            exit_code=124  # 超时退出码
        else
            wait $pid 2> /dev/null
            exit_code=$?
        fi

        # 输出结果
        if [ $exit_code -eq 0 ] || [ $exit_code -eq 124 ]; then
            echo "  ✓ $app - 运行正常"
            ((success++))
        else
            echo "  ✗ $app - 运行失败"
            ((failed++))
            failed_apps="$failed_apps $app"
        fi
    } &

    # 等待1秒再启动下一个
    sleep 1
done

# 等待所有后台测试完成
wait

echo ""
echo "=========================="
echo "🎉 测试完成!"
echo "=========================="
echo "成功: $success/$total ($((success * 100 / total))%)"
echo "失败: $failed 个"
echo ""

if [ $failed -gt 0 ]; then
    echo "失败的应用:"
    for app in $failed_apps; do
        echo "  - $app"
    done
fi

echo ""
echo "✅ 项目总体质量: $((success * 100 / total))% 成功率"

# 保存简单结果到文件
echo "$(date) - 成功率: $((success * 100 / total))% ($success/$total)" > test_result.txt
if [ $failed -gt 0 ]; then
    echo "失败: $failed_apps" >> test_result.txt
fi

echo "📊 结果已保存到 test_result.txt"
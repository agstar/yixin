package com.imooc.yixin;


import org.junit.Test;

import java.util.Arrays;

public class Sort {

    private int[] arr = new int[10];


    @Test
    public void test() {
        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) {
            arr[i] = (int) (Math.random() * 10);
        }
        System.out.println("排序前");
        System.out.println(Arrays.toString(arr));
        selectSort(arr);
        System.out.println("排序后");
        System.out.println(Arrays.toString(arr));
    }


    //    @Before
    public void before() {
        arr = new int[10];
        for (int i = 0; i < 10; i++) {
            arr[i] = (int) (Math.random() * 10);
            System.out.print(arr[i] + "\t");
        }

        selectSort(arr);
        System.out.println();
    }

    @Test
    public void test1() {
        System.out.println(Arrays.toString(arr));
        int key = (int) (Math.random() * 10);
        int rank = rank(key, arr);
        System.out.println(key + "在数组中的下标位" + rank);

    }


    private static int rank(int key, int nums[]) {
        // 查找范围的上下界
        int low = 0;
        int high = nums.length - 1;
        // 未查找到的返回值
        int notFind = -1;
        while (low <= high) {
            // 二分中点=数组左边界+(右边界-左边界)/2
            // 整数类型默认取下整
            int mid = low + (high - low) / 2;
            // 中间值是如果大于key
            if (nums[mid] > key) {
                // 证明key在[low,mid-1]这个区间
                // 因为num[mid]已经判断过了所以下界要减一
                high = mid - 1;
            } else if (nums[mid] < key) {
                // 证明key在[mid+1,high]这个区间
                // 同样判断过mid对应的值要从mid+1往后判断
                low = mid + 1;
            } else {
                // 查找成功
                return mid;
            }
        }
        // 未成功
        return notFind;
    }


    private static int binarySearch(int[] arr, int key) {
        int low = 0;
        int high = arr.length - 1;
        int notFind = -1;
        while (low <= high) {
            int middleIndex = low + (high - low) / 2;
            if (arr[middleIndex] > key) {
                high = middleIndex - 1;
            } else if (arr[middleIndex] < key) {
                low = middleIndex + 1;
            } else {
                return middleIndex;
            }
        }
        return notFind;
    }


    public static void selectSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[minIndex] > arr[j]) {
                    minIndex = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }


}

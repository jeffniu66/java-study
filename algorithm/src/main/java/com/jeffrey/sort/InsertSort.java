package com.jeffrey.sort;

public class InsertSort {
    
    /**
     * Insertion Sort Algorithm
     * Time Complexity: O(n²) - worst and average case
     * Space Complexity: O(1) - in-place sorting
     * Stability: Stable sort
     */
    public static void insertionSort(int[] arr) {
        int n = arr.length;
        
        // Start from the second element, as the first is considered sorted
        for (int i = 1; i < n; i++) {
            // Current element to be inserted
            int key = arr[i];
            // Start comparing from the last element of sorted part
            int j = i - 1;
            
            // Move elements greater than key one position ahead
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            
            // Insert key at its correct position
            arr[j + 1] = key;
            
            // Print current step
            System.out.println("After round " + i + ": " + arrayToString(arr));
        }
    }
    
    /**
     * Convert array to string for printing
     */
    private static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Test method
     */
    public static void main(String[] args) {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        
        System.out.println("=== Insertion Sort ===");
        System.out.println("Original array: " + arrayToString(arr));
        System.out.println("Starting insertion sort:");
        
        insertionSort(arr);
        
        System.out.println("Final sorted array: " + arrayToString(arr));
    }
}

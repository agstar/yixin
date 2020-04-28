 ```java  
 public String huiwen(String str) {
        String right = "Right";
        String wrong = "Wrong";
        Stack stack = new Stack<Character>();
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            stack.push(aChar);
        }
        boolean ishuiwen = true;
        for (char aChar : chars) {
            Character pop = (Character) stack.pop();
            if (!pop.equals(aChar)) {
                ishuiwen = false;
                break;
            }
        }
        return ishuiwen ? right : wrong;
    }``` 
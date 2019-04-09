import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.function.IntSupplier;

/**
 * 
 * S->SpA|A A->BmA|B|Aa|Ab B->a|b|C|lsr C->Dc D->SBb
 * 
 * 
 */

public class ReadFile {
    public static void main(String[] args) {
        BufferedReader reader;
        // Linked list for each production than insert it into an LinkedList of
        // Production
        // [S, SpA, A] is a linkedlist of all production from S
        // [S, SpA, A] save this LinkedList into an LinkedList of Production
        LinkedList<LinkedList<String>> li = new LinkedList<LinkedList<String>>();
        try {
            reader = new BufferedReader(new FileReader("javacode.txt"));

            /*
             * line by line read the file. All production from same Non Terminal must be at
             * same line using | char
             */
            String line = reader.readLine();
            while (line != null) {
                LinkedList<String> productionList = new LinkedList<String>();
                // split each line into Non-Terminal and its Productions by "->" and "|"
                // S->SpA|A into [S, SpA, A]
                String[] prodution = line.split("\\-\\>|\\|");
                // [S, SpA, A] insert each split into the productionList
                for (int i = 0; i < prodution.length; i++) {
                    productionList.add(prodution[i]);
                }
                // insert [S, SpA, A] which is LinkedList into the parent LinkedList of all
                // productions
                li.add(productionList);
                line = reader.readLine();
            }
            // print all production each index contains full production from Non Terminal
            System.out.println(li);

            // remove indirect left recursion

            LinkedList<LinkedList<String>> indLeftRecursion = new LinkedList<LinkedList<String>>();
            for (int i = 0; i < li.size(); i++) {
                String nTer = li.get(i).get(0);
                // System.out.println("\nNT " + nTer);
                for (int j = 1; j < li.get(i).size(); j++) {
                    String prod = li.get(i).get(j);
                    String pattern = "^" + nTer;
                    String[] removeLeft = prod.split(pattern);
                    if (removeLeft.length == 1) {
                        // alpha.add(removeLeft[1]);
                        // System.out.println(prod);
                        char leftNonTerminal = prod.charAt(0);
                        if (isUpperCase(leftNonTerminal + "")) {
                            int index = getNonTerminalIndex(leftNonTerminal + "", li);
                            // System.out.println(leftNonTerminal + "-" + index);
                            int k = index;
                            if (!(index < i)) {

                                for (; index < li.size();) {

                                    for (int l = 1; l < li.get(index).size(); l++) {
                                        String nProd = li.get(index).get(l);
                                        String[] nRemoveLeft = nProd.split(pattern);
                                        if (nRemoveLeft.length == 1) {
                                            leftNonTerminal = nProd.charAt(0);
                                            if (isUpperCase(leftNonTerminal + "")) {
                                                k = index;
                                                index = getNonTerminalIndex(leftNonTerminal + "", li);
                                                // System.out.println(leftNonTerminal + " " + index);
                                                break;
                                            }
                                        }
                                        // TODO non terminal found
                                        else {
                                            // System.out.println(leftNonTerminal + " hell " + index);
                                            li.get(index).remove(l);
                                            // System.out.println("--- ");
                                            for (int ll = 1; ll < li.get(i).size(); ll++) {
                                                String pp = li.get(i).get(ll);
                                                // System.out.print(pp + "" + nRemoveLeft[1] + ", ");
                                                li.get(index).add(pp + "" + nRemoveLeft[1]);
                                            }
                                            // System.out.println("\n--- ");
                                            index = li.size();
                                            break;
                                        }
                                    }
                                    if ((index == li.size()) || index < k) {
                                        // System.out.println("----------------");
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    else {
                        // beta.add(prod);
                    }
                }
            }

            System.out.println("\n--------Indirect Left Recursion-----------");
            System.out.println(li);
            System.out.println("\n--------Direct Left Recursion-----------");

            /* Time to find LeftRecursion if exist in any production */
            // This list is same as parent list of all production but removed Left Recursion
            LinkedList<LinkedList<String>> leftRecursion = new LinkedList<LinkedList<String>>();
            // Read parent linkedlist of all productions one by one
            for (int i = 0; i < li.size(); i++) {
                /*
                 * alpha list will contain all alphas in each index of parent linkedlist
                 * 
                 * [S, SpA, A] for example above list has 2 productions [SpA, A] where alpha is
                 * "pA"
                 * 
                 * beta will will contain all betas in each index of parent linkedlist in above
                 * example beta is "A"
                 * 
                 */
                LinkedList<String> alpha = new LinkedList<String>();
                LinkedList<String> beta = new LinkedList<String>();

                // nTer is the first element Non-Terminal in each index of parent linkedlist
                // in [S, SpA, A] case its is "S"
                String nTer = li.get(i).get(0);
                /*
                 * Now read next portion of the list which in case of [S, SpA, A] is SpA, A for
                 * detecting left recursion
                 * 
                 */
                for (int j = 1; j < li.get(i).size(); j++) {
                    /*
                     * read [S, SpA, A] each one by one first read "SpA" check starting of prod is
                     * "S" if Yes then split into 2 parts [0]=non-terminal [1] = Aplha and if there
                     * is No Split which means beta then it means there is no LeftRecursion which is
                     * handled by else part
                     */
                    String prod = li.get(i).get(j);
                    String pattern = "^" + nTer;
                    String[] removeLeft = prod.split(pattern);
                    if (removeLeft.length > 1) {
                        alpha.add(removeLeft[1]);
                    }

                    else {
                        beta.add(prod);
                    }
                }
                /*
                 * rAp for "S`" rSimple for "S"
                 */
                LinkedList<String> rAp = new LinkedList<>();
                LinkedList<String> rSimple = new LinkedList<>();
                char prime = '\'';
                // alpha 0 means there is no left recursion in the production
                if (alpha.size() > 0) {
                    rSimple.add(nTer);
                    /*
                     * attach each (beta)S` like AS`
                     * 
                     */
                    for (int k = 0; k < beta.size(); k++) {
                        rSimple.add(beta.get(k) + nTer + prime);
                    }
                    rAp.add(nTer + prime);
                    /*
                     * attach each (alpha)S` like pAS`
                     * 
                     */
                    for (int j = 0; j < alpha.size(); j++) {
                        rAp.add(alpha.get(j) + nTer + prime);
                    }
                    rAp.add("epsilon");
                    leftRecursion.add(rSimple);
                    leftRecursion.add(rAp);
                } else {
                    // If there is no left recursion in the list simply add it into the list
                    leftRecursion.add(li.get(i));
                }

            }
            System.out.println(leftRecursion);

        } catch (

        IOException e) {
            e.printStackTrace();
        }

    }

    private static boolean isUpperCase(String st) {
        for (int i = 0; i < st.length(); i++) {
            char var = st.charAt(i);
            if (!(var >= 'A' && var <= 'Z')) {
                return false;
            }
        }
        return true;
    }

    private static int getNonTerminalIndex(String t, LinkedList<LinkedList<String>> l) {
        int index = -1;
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).get(0).equals(t)) {
                return i;
            }

        }
        return index;
    }
}
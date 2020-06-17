/**
 * Code from:
 * https://github.com/tpanetti/QuadSieve
 */

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
/*
** This program will run a quadratic sieve to
** factor some numbers
** @author Thomas Panetti & Noemi Glaeser
*/
 
public class QuadraticSieve
{
  //This is the range the matrix runs on.
  //50,000 works great because it works for all numbers 
  //in the range of long without being too large
  private static final long SIEVERANGE = 50000;

  /**
   * This is a private class
   * to be used as a touple for
   * saving an ArrayList and it's integer index
   * and some other things
   */
  private static class Pair<X, Y> 
  {
    private final X x;
    private final Y y;
    public Pair(X x, Y y) 
    {
      this.x = x;
      this.y = y;
    }
    public X getX(){ return x;}
    public Y getY(){ return y;}
  }
  
  /**
  * This method will perform an eratostheness Sieve to calculate all primes
  * under the input factobase
  *
  * @param size This is the size to create the array of primes
  * @return 	An ArrayList of primes under size 
  */        
  public static ArrayList<Integer> eratosthenesSieve(long size) {
    ArrayList<Integer> primes = new ArrayList<Integer>();
    // populate a list with i = 2 to n
    for (int i = 2; i < size; i++)
      primes.add(i);

    // set p equal to 2?
    int p = 2;
    // loop
    while (Math.pow(p, 2) < size) {
      for (int i = primes.indexOf(p) + 1; i < primes.size(); i++) {
        //// System.out.println(primes.get(i) +": divided by " + p);
        if (primes.get(i) % p == 0) {
          primes.remove(i);
        }
      }
      p++;
    }
    primes.add(0, -1);

    return primes;
  }
  
  /**
  *This method will find the smooth numbers between the range
  *specified
  *
  *@param numToFactor 	the input number we are factoring
  *@return 	An ArrayList of longs representing the smooth values found
  */
  public static ArrayList<Long> findSmoothness(long numToFactor) {
    // Offset = R. Change later
    ArrayList<Long> Qs = new ArrayList<Long>();
    // System.out.println("Root is " + Math.sqrt(numToFactor)
    // + "\tindex\t" + "(r+n)^2\t" + "(r+n)^2 - N)");
    for (long i = -SIEVERANGE; i <= SIEVERANGE; i++) {
      // store log of Q possibly
      long Q = ((long) (Math.pow((Math.sqrt(numToFactor) + i), 2)));

      long save = Q - numToFactor;
      Qs.add(save);
      // System.out.println(i +"\t" + (Qs.size() - 1) + "\t" + Q + "\t" + save);
    }
    return Qs;
  }
  
  /**
  *This method will determine which numbers are smooth by repeatedly dividing
  *by primes in the factor base to obtain residuals of each number. 
  *
  *@param smoothlist An ArrayList of smooth 
  *@return 	An ArrayList of longs representing the newly calculated residuals
  */
  public static ArrayList<Long> calcResiduals(ArrayList<Integer> primes, ArrayList<Long> smoothlist) {
    // ArrayList<Pair<Integer, Integer>> residuals
    // = new ArrayList<Pair<Integer,Integer>>();

    ArrayList<Long> copy = new ArrayList<Long>();
    for (long i : smoothlist)
      copy.add(i);
    int start;
    for (int i : primes) {
      if (i == -1)
        continue;
      // System.out.println("Sieve with " + i);
      for (int j = 0; j < copy.size(); j++) {
        if (copy.get(j) % i == 0) {
          start = j;
          int index = j;
          do {
            long temp = copy.get(j);
            while (temp != 0 && temp % i == 0) {
              temp = temp / i;
            }
            copy.set(j, temp);
            j = (j + i) % copy.size();
          } while (j != start);
        }
      }
    }
    // Take abs value of the array
    for (int i = 0; i < copy.size(); i++)
      copy.set(i, Math.abs(copy.get(i)));

    return copy;

  }

  /**
  *This method will create a matrix of the prime factorization of numbers
  *that are smooth over the factorbase.
  *It stores the exponents of each prime factor of each number.
  *
  *@param Residues	An ArrayList of reisdues calculated from calcResidues
  *@param original	the original ArrayList of the smooth numbers
  *@param primes 	the list of primes up to the factorbase	
  *@return 	an ArrayList of pairs containing the rows and their corresponding integer index in case the rows get rearranged later
  */
  public static ArrayList<Pair<ArrayList<Long>, Integer>> refactor(ArrayList<Long> residues, ArrayList<Long> original,
      ArrayList<Integer> primes) {
    // initialize arraylist and fill with zero arryalists
    ArrayList<Pair<ArrayList<Long>, Integer>> exponents = new ArrayList<Pair<ArrayList<Long>, Integer>>();
    // cheating
    long zero = 0;
    for (int i = 0; i < residues.size(); i++) {
      if (residues.get(i) == 1) {
        ArrayList<Long> exponent = new ArrayList<Long>();
        for (int index = 0; index < primes.size(); index++)
          exponent.add(zero);
        long temp = original.get(i);
        if (temp < 0) {
          temp = temp * -1;
          exponent.set(0, new Long(1));
        } // pIndex set to 1 to skip the "prime" -1
        for (int pIndex = 1; pIndex < primes.size(); pIndex++) {
          while (temp % primes.get(pIndex) == 0) {
            temp = temp / primes.get(pIndex);
            exponent.set(pIndex, (exponent.get(pIndex)) + 1);
          }

        }
        exponents.add(new Pair<ArrayList<Long>, Integer>(exponent, i));
      }

    }

    return exponents;
  }

  /**
  *This method reduces the prime factorization matrix mod 2 (so the matrix only stores the parity of the exponents).
  *
  *@param 	An ArrayList of Pairs representing the matrix and the indices
  *@return 	An ArrayList of Pairs of ArrayLists and their corresponding indices (in case of the loss of the index from reduction
  */
  public static ArrayList<Pair<ArrayList<Integer>, Integer>> reduceModTwo( ArrayList<Pair<ArrayList<Long>, Integer>> matrix) {
    ArrayList<Pair<ArrayList<Integer>, Integer>> modTwo = new ArrayList<Pair<ArrayList<Integer>, Integer>>();
  
    for (int i = 0; i < matrix.size(); i++) {

      for (int j = 0; j < matrix.get(i).getX().size(); j++) {
        matrix.get(i).getX().set(j, matrix.get(i).getX().get(j) % 2);

      }
    }
    for (Pair<ArrayList<Long>, Integer> pair : matrix) {
      ArrayList<Integer> ints = new ArrayList<Integer>();
      for (long number : pair.getX())
        ints.add((int) number);
      modTwo.add(new Pair<ArrayList<Integer>, Integer>(ints, pair.getY()));
    }
    return modTwo;
  }

  /**
  *This method uses Gaussian elimination to return a list
  *of row combinations that sum to zero. These represent
  *products that are square and can be used to rebuild the equation x^2 = y^2.
  *
  *@param array	The array to perform gauss elimination on
  *@returns An ArrayList of ArrayLists representing the row reduction operations performed on the matrix
  */
  public static ArrayList<ArrayList<Integer>> gauss(ArrayList<Pair<ArrayList<Integer>, Integer>> array) {
    ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> reducedMatrix = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>();
    // initialize ArrayList
    for (int i = 0; i < array.size(); i++) {
      ArrayList<Integer> rowReductions = new ArrayList<Integer>();
      // first value in the index is the original index of said row
      rowReductions.add(array.get(i).getY());
      reducedMatrix.add(new Pair<ArrayList<Integer>, ArrayList<Integer>>(array.get(i).getX(), rowReductions));
    }
    int j = 0;
    for (int i = 0; i < reducedMatrix.size(); i++) {
      if (j >= reducedMatrix.get(i).getX().size())
        break;
      if (reducedMatrix.get(i).getX().get(j) == 0) {
        // index of the next row that has a 1 to swap for the zero
        int oneRow;
        for (oneRow = i + 1; oneRow < reducedMatrix.size(); oneRow++) {
          // break at 1 to preserve oneRow for swapping
          if (reducedMatrix.get(oneRow).getX().get(j) == 1)
            break;
        }
        // if it's not equal to the size, we did find a 1
        if (oneRow < reducedMatrix.size()) { // one found, swap rows
          Pair<ArrayList<Integer>, ArrayList<Integer>> temp = reducedMatrix.get(i);
          reducedMatrix.set(i, reducedMatrix.get(oneRow));
          reducedMatrix.set(oneRow, temp);
        }
        // else move to the next column
        else {
          i--;
          j++;
          continue;
        }
      }

      // else we have 1, zero out the column
      for (int row = i + 1; row < reducedMatrix.size(); row++) {
        if (reducedMatrix.get(row).getX().get(j) == 0)
          continue;
        reducedMatrix.get(row).getX().set(j, 0);
        reducedMatrix.get(row).getY().add(i);
      }

      j++;
    }

    ArrayList<ArrayList<Integer>> zeroSums = new ArrayList<ArrayList<Integer>>();

    for (int i = 0; i < reducedMatrix.size(); i++) {
      for (j = 0; j < reducedMatrix.get(i).getX().size(); j++) {
        if (reducedMatrix.get(i).getX().get(j) != 0) {
          // break out of the loop by ending condition
          j = reducedMatrix.get(i).getX().size();
          continue;
        }
      }
      zeroSums.add(reducedMatrix.get(i).getY());
    }
    return zeroSums;
  }
  
  /**
  * This method will rebuild the equation x^2 = y^2
  *
  * @param bigN		The number to be factored
  * @param gaussHits	The matrix of row reduce operations from gauss
  * @return			A HashSet containing the factors of N
  */
  public static HashSet<Long> rebuildEquation(ArrayList<ArrayList<Integer>> gaussHits, long bigN) {
    long R = ((long) (Math.sqrt(bigN))) / SIEVERANGE;
    HashSet<Long> factors = new HashSet<Long>();

    for (ArrayList<Integer> hit : gaussHits) {
      int lhs = 1;
      int rhs = 1;
      for (int num = 1; num < hit.size(); num++) {
        lhs *= Math.pow(R + (hit.get(num) - R), 2);
        rhs *= Math.pow(R + (hit.get(num) - R), 2) - bigN;
      }
      long x = (long) (Math.sqrt(Math.abs(lhs)));
      long y = (long) (Math.sqrt(Math.abs(rhs)));
      if (x == y)
        continue;
      long factor1 = (gcd(bigN, (x + y)));
      long factor2 = (gcd(bigN, (x - y)));

      factors.add(Math.abs(factor1));
      factors.add(Math.abs(factor2));
      // check if the factors make other factors!
      if (bigN % factor1 == 0)
        factors.add(Math.abs(bigN / factor1));
      if (bigN % factor2 == 0)
        factors.add(Math.abs(bigN / factor2));
      if (factors.size() > 1)
        factors = completeFactors(factors);
    }

    return factors;
  }
  
  /***
  *This method divides all factors by other factors to ensure 
  *that all prime factors are listed in the output.
  *
  *@param factors	This is the list of factors so far
  *@return A HashSet of all factors factored from the original	
					factors
  */
  public static HashSet<Long> completeFactors(HashSet<Long> factors) {
    HashSet<Long> newFactors = new HashSet<Long>();
    for (long fact : factors)
      newFactors.add(fact);
    for (long fact : factors)
      for (long fact2 : factors)
        if (fact > fact2 && fact % fact2 == 0)
          newFactors.add(fact / fact2);
    return newFactors;
  }
  
  /**
  * This method will run the euclidean algorithm to
  * factor two integer into their greatest common denominator
  *
  *@param a 	An integer to be reduced
  *@param b 	An integer to be reduced
  *@return	The greatest common denominator between a and b
  */
  public static long gcd(long a, long b) {
    while (b != 0) {
      long t = b;
      b = a % b;
      a = t;
    }
    return a;
  }

  /**
   * function main
   * @param args
   */
  public static void main(String[] args) {
    
    long n; // Grab number N to be factored
    
    long factorbase; // Grab the factor base
    
    // take from command line
    // if no factobase is given, assume 1000
    if (args.length == 1) {
      n = Long.parseLong(args[0]);
      factorbase = 1000;
    } else if (args.length == 2) {
      n = Long.parseLong(args[0]);
      factorbase = Long.parseLong(args[1]);
    }
    // else take from scanner
    else {
      Scanner input = new Scanner(System.in);
      System.out.println("Ingresa N: ");
      n = input.nextLong();
      System.out.println("Ingresa los factores base: ");
      factorbase = input.nextLong();
    }
    // Find R
    long R = (long) Math.sqrt(n);

    // generate a list of primes up to factorbase
    ArrayList<Integer> primes = eratosthenesSieve(factorbase);
    // generate a list that satisfies the eqn
    ArrayList<Long> bSmooth = findSmoothness(n);
    ArrayList<Long> residues = calcResiduals(primes, bSmooth);

    // Refactor and Gauss
    ArrayList<Pair<ArrayList<Long>, Integer>> refactoredResidual = refactor(residues, bSmooth, primes);
    // reduce the matrix mod 2
    ArrayList<Pair<ArrayList<Integer>, Integer>> reducedResidual = reduceModTwo(refactoredResidual);

    ArrayList<ArrayList<Integer>> gaussHits = gauss(reducedResidual);
    // rebuild equation
    HashSet<Long> factors = rebuildEquation(gaussHits, n);
    System.out.print("Los factores son: ");
    for (long factor : factors)
      System.out.print(factor + ", ");
    System.out.println();

  }



}
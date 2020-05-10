package com.ysingh.elevator;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResultMessage {

  @Builder.Default
  private List<String> steps = new ArrayList<>(0);
  @Builder.Default
  private List<Integer> floorsVisited = new ArrayList<>(0);
  private int totalFloorsVisited;

}

package jb.model

abstract class MappingModel

case class PreTraining() extends MappingModel
case class PostTrainingCV() extends MappingModel
case class PostTrainingTrain() extends MappingModel
case class PostTrainingTrainFiltered() extends MappingModel

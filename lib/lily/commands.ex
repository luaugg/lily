defmodule Lily.Commands do
  use Supervisor

  def start_link(init \\ :ok), do:
    Supervisor.start_link(__MODULE__, init, name: __MODULE__)

  def init(_init) do
    children = [Lily.Commands.Server, Lily.Commands.List]
    Supervisor.init(children, strategy: :one_for_one)
  end
end